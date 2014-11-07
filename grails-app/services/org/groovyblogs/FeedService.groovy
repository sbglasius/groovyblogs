package org.groovyblogs

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.ParsingFeedException
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import net.sf.ehcache.Element
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.params.HttpClientParams

@Transactional()
class FeedService {

    def grailsApplication
    def listCache
    def pendingCache
    def tweetCache
    ThumbnailService thumbnailService
    TranslateService translateService
    TwitterService twitterService
    def mailService
    def groovyPageRenderer
    def grailsLinkGenerator

    // Returns the HTML for the supplied URL
    @NotTransactional
    String getHtmlForUrl(url) {

        log.info("Trying to fetch [$url]")

        def client = new HttpClient()
        def clientParams = client.getParams()
        clientParams.setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, "UTF-8")

        if (config.http.useproxy) {
            def hostConfig = client.getHostConfiguration()
            hostConfig.setProxy(config.http.host, config.http.port as int)
            log.warn("Setting proxy to [$config.http.host]")
        }

        if (config.http.useragent) {
            clientParams.setParameter(HttpClientParams.USER_AGENT, config.http.useragent)
        }

        if (config.http.timeout) {
            clientParams.setParameter(HttpClientParams.SO_TIMEOUT, config.http.timeout)
        }

        def mthd = new GetMethod(url)

        def statusCode = client.executeMethod(mthd)
        def responseBody = mthd.getResponseBody()
        mthd.releaseConnection()

        log.debug("Fetched [$url] successfully")

        new String(responseBody)
    }

    // takes a URL and returns ROME feed info
    @NotTransactional
    FeedInfo getFeedInfo(String feedUrlStr, boolean translate = false) {
        def feedStr = getHtmlForUrl(feedUrlStr)
        def syndFeedInput = new SyndFeedInput()
        def bais = new ByteArrayInputStream(feedStr.getBytes("UTF-8"))
        def feedReader = new XmlReader(bais, true)
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
    }

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

                BlogEntry be = new BlogEntry(title: entry.title, link: entry.link,
                        description: entry.description,
                        language: entry.language,
                        hash: entry.summary.encodeAsMD5())

                if (be.isGroovyRelated()) {
                    //log.info("Added new entry: $be.title")

                    try {
                        blog.addToBlogEntries(be)
                        if (!be.validate()) {
                            log.warn("Validation failed updating blog entry [$be.title]")
                            be.errors.allErrors.each {
                                log.warn(it)
                            }
                        } else {
                            be.save(flush: true)
                            blog.save(flush: true)

                            try {

                                if (config.twitter.enabled) {
                                    twitterService.sendTweet("${be.title} -- ${be.link} -- ${blog.title}")
                                }

                                if (config.thumbnail.enabled) {
                                    // be.thumbnail = thumbnailService.fetchThumbnail(be.link)
                                    // log.debug "Adding to pending thumbs cache: ${be?.link}"
                                    //pendingCache.put( new Element(be.link, be.id))
                                }
                            } catch (e) {
                                log.debug "Error during thumbnail collection", e
                            }
                        }
                    } catch (t) {
                        log.error t.message, t
                    }

                    log.debug("Saved entry with title [$be.title]")
                } else {
                    log.debug("Ignoring non-groovy blog entry: $be.title")
                }
            }
        }

        blog.lastPolled = new Date()
        long nextPollTime = System.currentTimeMillis() + blog.pollFrequency * 60 * 60 * 1000
        blog.nextPoll = new Date(nextPollTime)
        blog.errorCount = 0
        blog.lastError = ''
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

    void updateFeed(Blog blog) {

        log.info("Now polling: [$blog.title]")
        FeedInfo fi
        try {
            fi = getFeedInfo(blog.feedUrl, config.translate.enabled)
        } catch (e) {
            log.warn("Could not parse feed [$blog.feedUrl]", e)
            markBlogWithError(blog, e)
        }
        updateFeed(blog, fi)
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
                markBlogUpdateSuccess(blog)
            } catch (e) {
                log.warn("FeedService failed to update $blog", e)
                markBlogWithError(blog, e)
            }
        }

        log.info("FeedService finished polled update")
    }

    def markBlogWithError(Blog blog, Exception e) {
        blog.lastError = "Error parsing [$blog.feedUrl] $e.message"
        blog.errorCount++
        log.warn("Encountered error in [$blog.feedUrl]. This is error number $blog.errorCount")
        if (blog.errorCount > config.groovyblogs.maxErrors ?: 10) {
            log.info("FeedService marked blog $blog with ERROR")
            blog.status = BlogStatus.ERROR
        }
        blog.save()
    }

    void markBlogUpdateSuccess(Blog blog) {
        log.info("FeedService marked blog $blog ACTIVE")
        blog.lastError = ''
        blog.status = BlogStatus.ACTIVE
        blog.errorCount = 0
    }

    def updateLists() {

        def allEntries = []

        config.lists.each { name, url ->

            log.info("Updating list [$name] from [$url]")
            def feed = getFeedInfo(url, false)

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

        // sort in date desc
        allEntries = allEntries.sort { e1, e2 -> e2.publishedDate <=> e1.publishedDate }

        log.debug("Putting to list cache: ${allEntries.size()}")

        listCache.put(new Element("listEntries", allEntries))

        return allEntries
    }

    def getCachedListEntries() {
        listCache.get("listEntries")?.value ?: updateLists()
    }

    def updateTweets() {
        if (!config.tweets.enable) {
            return
        }

        def tweetFeed = getFeedInfo(config.tweets.url, false)

        def allEntries = tweetFeed.entries.collect { entry ->
            // entry.description = entry.description.replaceFirst("[^:]+:\\s*", "")
            entry
        }

        log.debug("Putting to tweet cache: ${allEntries.size()}")

        tweetCache.put(new Element("tweetEntries", allEntries))

        return allEntries
    }

    def getCachedTweetEntries() {
        tweetCache.get("tweetEntries")?.value ?: updateTweets()
    }

    @Transactional(noRollbackFor = [ParsingFeedException])
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
                new BlogEntry(title: entry.title, description: entry.description).isGroovyRelated()
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
    Blog testFeed(String feedUrl) {
        def feedInfo = getFeedInfo(feedUrl)

        def blog = createBlogFromFeedInfo(feedInfo)
        feedInfo.entries.each {
            blog.addToBlogEntries(title: it.title, description: it.description)
        }

        return blog
    }

    /**
     * Create a blog from the feed url
     * @param feedUrl
     * @param account
     * @return
     */
    Blog createBlog(String feedUrl, User account = null) {
        def feedInfo = getFeedInfo(feedUrl)

        return createBlogFromFeedInfo(feedInfo, account)
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
}
