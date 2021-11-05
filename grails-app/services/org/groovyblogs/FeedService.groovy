package org.groovyblogs

import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.SyndFeedOutput
import com.rometools.rome.io.XmlReader
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import grails.util.Environment
import net.sf.ehcache.Element

class FeedService implements EventPublisher {

    def grailsApplication
    def listCache
    def feedCache
    def tweetCache
    TranslateService translateService
    TwitterService twitterService
    def mailService
    def groovyPageRenderer
    def grailsLinkGenerator

    // Returns the HTML for the supplied URL
    String getHtmlForUrl(String url) {

        log.info("Trying to fetch [$url]")
        def html = url.toURL().getText('UTF-8')

        log.debug("Fetched [$url] successfully")
        return html
    }

    // takes a URL and returns ROME feed info
    FeedInfo getFeedInfo(String feedUrlStr, boolean translate = false) {
        try {
            def syndFeedInput = new SyndFeedInput()
            syndFeedInput.xmlHealerOn = true
            def url = feedUrlStr.toURL().newInputStream()
            def feedReader = new XmlReader(url, true,'UTF-8')
            SyndFeed syndFeed = syndFeedInput.build(feedReader)
            def feedInfo = new FeedInfo(feedUrl: feedUrlStr, title: syndFeed.title,
                    description: syndFeed.description ?: "",
                    author: syndFeed.author, type: syndFeed.feedType)
            for (SyndEntry entry in syndFeed.entries) {
                String title = entry.title
                String description = entry.description?.value
                if (!description) {  // mustn't be rss... could be atom
                    description = entry.contents[0]?.value
                }
                // trim to 4k-ish size for db storage
                if (description?.length() > 4000) {
                    description = description[0..3999]
                }
                String link = entry.link
                Date publishedDate = entry.publishedDate
                def summary
                if (description) {
                    // strip html for the summary, then truncate
                    summary = description.replaceAll("</?[^>]+>", "")
                    summary = summary.length() > 200 ? summary[0..199] : summary
                }

                def feedEntry = new FeedEntry(title: title, link: link, publishedDate: publishedDate,
                        description: description ?: "",
                        summary: summary ?: "",
                        author: entry.author ?: "")

                //TODO ignore stuff older than X days
                def trimEntriesOlderThanXdays = config.feeds.ignoreFeedEntriesOlderThan
                if (trimEntriesOlderThanXdays) {
                    def trimTime = new Date() - trimEntriesOlderThanXdays // X days ago
                    if (publishedDate && publishedDate < trimTime) {
                        log.debug("Skipping old entry: [$title] from [$publishedDate]")
                        feedEntry = null // too old to include
                    }
                }

                if (feedEntry) {
                    if (translate) {
                        feedEntry.language = translateService.getLanguage(description)
                    }
                    log.debug("Read entry with title [$title] and link [$link]")
                    feedInfo.entries.add(feedEntry)
                }
            }
            return feedInfo
        } catch (e) {
            log.warn("Error parsing ${feedUrlStr}: ${e.message}",e)
            return null
        }

    }

    @Transactional()
    void updateFeed(Blog blog, FeedInfo fi) {

        //def existingEntries = blog.blogEntries

        // we iterate in reverse to ensure newest entries have the newest timestamps
        fi?.entries?.reverseEach { entry ->

            log.debug("Looking for $entry.link")
            //def existing = existingEntries.find { entry.link == it.link }
            def existing = BlogEntry.findByHash(entry.summary.encodeAsMD5().toString()) || BlogEntry.findByLink(entry.link)
            log.debug("Existing? $existing")
            ///if (!BlogEntry.findByLink(entry.link)) {
            if (!existing) {

                //log.debug("Creating entry with title [$entry.title] and link [$entry.link]")

                BlogEntry blogEntry = new BlogEntry(title: entry.title, link: entry.link,
                        description: entry.description,
                        language: entry.language,
                        hash: entry.summary.encodeAsMD5(),
                        sourceStatus: 200,
                        sourceStatusDate: new Date())

                if (blogEntry.groovyRelated) {
                    //log.info("Added new entry: $be.title")
                    try {
                        if (!blogEntry.validate()) {
                            log.warn("Validation failed updating blog entry [$blogEntry.title]")
                            blogEntry.errors.allErrors.each {
                                log.warn(it.toString())
                            }
                        } else {
                            blog.addToBlogEntries(blogEntry)
                            blogEntry.save(flush: true)
                            blog.save(flush: true)

                            if (config.twitter.enabled) {
                                twitterService.sendTweet("${blogEntry.title} -- ${blogEntry.link} -- ${blog.title}")
                            }

                            if (config.thumbnail.enabled) {
                                notify('requestThumbnail', blogEntry)
                            }
                        }
                    } catch (t) {
                        log.error t.message, t
                    }

                    log.debug("Saved entry with title [$blogEntry.title]")
                } else {
                    log.debug("Ignoring non-groovy blog entry: $blogEntry.title")
                }
            }
        }

        blog.lastPolled = new Date()
        long nextPollTime = System.currentTimeMillis() + blog.pollFrequency * 60 * 60 * 1000
        blog.nextPoll = new Date(nextPollTime)
        if (!blog.validate()) {
            log.warn("Validation failed updating blog [$blog.title]")
            blog.errors.allErrors.each {
                log.warn(it)
            }
        } else {
            blog.save(flush: true)
            log.debug("Updated poll time for blog: $blog")
        }
        log.debug("Next poll of [$blog.title] at $blog.nextPoll")
    }

    @Transactional()
    void updateFeed(Blog blog) {
        log.info("Now polling: [$blog.title]")
        FeedInfo fi = getFeedInfo(blog.feedUrl, config.translate.enabled)
        if (fi) {
            updateFeed(blog, fi)
            markBlogUpdateSuccess(blog)
        } else {
            log.warn("Could not parse feed [$blog.feedUrl]")
            markBlogWithError(blog)
        }
    }

    protected ConfigObject getConfig() {
        grailsApplication.config
    }


    void updateFeeds() {

        log.info("FeedService starting polled update")
        def feedsToUpdate = Blog.findAllByStatusAndNextPollLessThan(BlogStatus.ACTIVE, new Date())
        log.info("${feedsToUpdate.size()} to update")

        // Limit to 5 updated blogs per minute. Could probably up this significantly
        // by going multithreaded...
        if (feedsToUpdate.size() > config.http.maxpollsperminute) {
            log.warn("${feedsToUpdate.size()} exceeds max for this minute. Limiting update to ${config.http.maxpollsperminute}.")
            feedsToUpdate = feedsToUpdate[0..config.http.maxpollsperminute - 1]
        }

        feedsToUpdate.each { blog ->
            try {
                updateFeed(blog)
            } catch (Exception e) {
                log.warn("FeedService failed to update $blog.title: ${e.message}")
                markBlogWithError(blog, e)
            }
        }

        log.info("FeedService finished polled update")
    }

    @Transactional()
    def markBlogWithError(Blog blog, Exception e = null) {
        blog.lastError = "Error parsing [$blog.feedUrl] ${e?.message ?: ''}"
        blog.errorCount = blog.errorCount + 1
        log.warn("Encountered error in [${blog.feedUrl}]. This is error number ${blog.errorCount}")
        if (blog.errorCount > (config.groovyblogs.maxErrors ?: 10)) {
            log.info("FeedService marked blog $blog.title with ERROR")
            blog.status = BlogStatus.ERROR
        }
        blog.save(failOnError: true)
    }

    @Transactional()
    void markBlogUpdateSuccess(Blog blog) {
        log.info("FeedService marked blog $blog.title ACTIVE")
        blog.lastError = ''
        blog.status = BlogStatus.ACTIVE
        blog.errorCount = 0
        blog.save()
    }

    @Transactional()
    def updateLists() {

        def allEntries = []

        config.lists.each { name, url ->

            log.info("Updating list [$name] from [$url]")
            def feed = getFeedInfo(url, false)
            if (feed) {
                Blog listBlog = new Blog(title: feed.title)

                def filter = new Date() - 1 // 1 days ago

                // Add 8 hours from Nabble feed time...
                def rightDates = feed.entries.collect { entry ->
                    def diff = entry.publishedDate.time + 1000 * 60 * 60 * 7
                    entry.publishedDate = new Date(diff)
                    return entry
                }
                def feedEntries = rightDates.findAll { entry -> entry.publishedDate.after(filter) }
                log.info "Filtered original entries from ${feed.entries.size()} to ${feedEntries.size()}"
                feedEntries.each { entry ->
                    entry.info = name
                    allEntries << entry
                }
            }

        }

        // sort in date desc
        allEntries = allEntries.sort { it.publishedDate }

        log.debug("Putting to list cache: ${allEntries.size()}")

        listCache.put(new Element("listEntries", allEntries))

        return allEntries
    }

    @Transactional()
    def getCachedListEntries() {
        listCache.get("listEntries")?.value ?: updateLists()
    }

    @Transactional()
    def updateTweets() {
        if (!config.tweets.enable) {
            return
        }

        def tweetFeed = getFeedInfo(config.tweets.url, false)
        if (tweetFeed) {
            def allEntries = tweetFeed.entries.collect { entry ->
                // entry.description = entry.description.replaceFirst("[^:]+:\\s*", "")
                entry
            }

            log.debug("Putting to tweet cache: ${allEntries.size()}")

            tweetCache.put(new Element("tweetEntries", allEntries))

            return allEntries
        }
    }

    def getCachedTweetEntries() {
        tweetCache.get("tweetEntries")?.value ?: updateTweets()
    }

    @Transactional()
    int checkPendingBlogs(List<Blog> blogs) {

        Map<Blog, FeedInfo> feedInfos = blogs.collectEntries { Blog it ->
            try {
                [it, getFeedInfo(it.feedUrl)]
            } catch (e) {
                it.status = BlogStatus.ERROR
                it.lastError = "Error parsing [$it.feedUrl] $e.message"
                it.save()
                [it, null]
            }
        }.findAll { it.value }

        feedInfos.each { Blog blog, FeedInfo feedInfo ->
            // Find if any of the blogs entries currently contains something Groovy related
            log.debug("Checking $blog.title")

            boolean hasGroovyContent = feedInfo.any { entry ->

                BlogEntry blogEntry = new BlogEntry(title: entry.title, description: entry.description)
                blogEntry.groovyRelated
            }
            blog.status = hasGroovyContent ? BlogStatus.LOOKS_GOOD : BlogStatus.NO_GROOVY
            blog.save()
        }

        feedInfos.size()
    }

    /**
     * Will return an unsaved Blog with accompanying BlogEntries
     * @param feedUrl
     * @return
     */
    @Transactional()
    Blog testFeed(String feedUrl) {
        def feedInfo = getFeedInfo(feedUrl)
        if (feedInfo) {
            def blog = createBlogFromFeedInfo(feedInfo)
            feedInfo.entries.each {
                blog.addToBlogEntries(title: it.title, description: it.description)
            }

            return blog
        }
    }

    /**
     * Create a blog from the feed url
     * @param feedUrl
     * @param account
     * @return
     */
    @Transactional()
    Blog createBlog(String feedUrl, User account = null) {
        def feedInfo = getFeedInfo(feedUrl)
        if (feedInfo) {
            return createBlogFromFeedInfo(feedInfo, account)

        }
    }

    private Blog createBlogFromFeedInfo(FeedInfo feedInfo, User account = null) {
        def blog = new Blog()
        blog.feedUrl = feedInfo.feedUrl
        blog.type = feedInfo.type
        blog.title = feedInfo.title ? feedInfo.title : ""
        blog.title = blog.title.length() > 250 ? blog.title[0..249] : blog.title
        blog.description = feedInfo.description ? feedInfo.description : ""
        blog.description = blog.description.length() > 250 ? blog.description[0..249] : blog.description

        blog.account = account
        return blog
    }

    @Transactional()
    boolean saveBlog(Blog blog) {
        blog.save(flush: true)
        if (grailsApplication.config.feeds.moderate) {
            blog.status = BlogStatus.PENDING
            try {
                def approve = grailsLinkGenerator.link(controller: 'account', action: 'approveFeed', id: blog.id, absolute: true)
                def reject = grailsLinkGenerator.link(controller: 'account', action: 'removeFeed', id: blog.id, absolute: true)
                mailService.sendMail {
                    to grailsApplication.config.feeds.moderator_email
                    subject "groovyblogs: Feed approval for ${blog.title}"
                    body groovyPageRenderer.render(template: '/mailtemplates/moderateFeed', model: [blog: blog, approve: approve, reject: reject])
                }

            } catch (Exception e) {
                log.error "Could not add feed", e
                blog.delete()
                return false
            }
        } else {
            updateFeed(blog)
            blog.status = BlogStatus.ACTIVE
        }

        return blog.save(failOnError: true)
    }

    @Transactional(readOnly = true)
    String getFeedData(feedType) {

        SyndFeed feed = feedCache.get("romeFeed-$feedType")?.value

        if (!feed) {
            // def blogEntries = BlogEntry.listOrderByDateAdded(max: 30, order: "desc")
            def aWhileAgo = new Date() - 7 // 7 days ago

            def blogEntries = BlogEntry.findAllByDateAddedGreaterThan(
                    aWhileAgo, [sort: 'dateAdded', order: "desc"])

            blogEntries = blogEntries.findAll { it.groovyRelated && it.sourceAvailable && !it.disabled }

            def feedEntries = blogEntries.collect { blogEntry ->
                def desc = new SyndContentImpl(type: "text/plain", value: FeedEntry.summarize(blogEntry.description))
                new SyndEntryImpl(title: blogEntry.title,
                        link: 'http://www.groovyblogs.org/entries/jump?id=' + blogEntry.id,
                        publishedDate: blogEntry.dateAdded, description: desc, author: blogEntry.blog.title)
            }
            feed = new SyndFeedImpl(feedType: feedType, title: 'GroovyBlogs.org',
                    link: 'http://www.groovyblogs.org', description: 'groovyblogs.org Recent Entries',
                    entries: feedEntries)

            feedCache.put(new Element("romeFeed-" + feedType, feed))
        }

        SyndFeedOutput output = new SyndFeedOutput()
        new StringWriter().withWriter { writer ->
            output.output(feed, writer, Environment.current == Environment.DEVELOPMENT)
            return writer.toString()
        }
    }
}
