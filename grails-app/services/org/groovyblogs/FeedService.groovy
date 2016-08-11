package org.groovyblogs

import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.ParsingFeedException
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.SyndFeedOutput
import com.rometools.rome.io.XmlReader
import grails.core.GrailsApplication
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import grails.util.Environment
import net.sf.ehcache.Element
import org.grails.exceptions.reporting.DefaultStackTraceFilterer
import org.springframework.transaction.annotation.Propagation

import javax.annotation.PostConstruct

@Transactional()
class FeedService {

    public static final String FEED_UPDATE = 'feed.update'
    public static final String BLOG_MARK_SUCCESS = 'blog.mark.success'
    public static final String BLOG_MARK_ERROR = 'blog.mark.error'
    GrailsApplication grailsApplication
    def listCache
    def feedCache
    def tweetCache
    TranslateService translateService
    TwitterService twitterService
    def mailService
    def groovyPageRenderer
    def grailsLinkGenerator

    // Returns the HTML for the supplied URL
    @NotTransactional
    String getHtmlForUrl(String url) {

        log.info("Trying to fetch [$url]")
        def html = url.toURL().getText('UTF-8')

        log.debug("Fetched [$url] successfully")
        return html
    }

    // takes a URL and returns ROME feed info
    @NotTransactional
    FeedInfo getFeedInfo(String feedUrlStr, boolean translate = false) {
        def feedContent = feedUrlStr.toURL().readLines().collect { line ->
            line.replaceAll(/<hr\s+\/>/, '<hr></hr>')
        }.join('\n')
        def inputStream = new ByteArrayInputStream(feedContent.bytes)
        def feedReader = new XmlReader(inputStream)

        def syndFeedInput = new SyndFeedInput()
        syndFeedInput.xmlHealerOn = true
        SyndFeed syndFeed = syndFeedInput.build(feedReader)
        def feedInfo = new FeedInfo(
                feedUrl: feedUrlStr,
                title: syndFeed.title,
                description: syndFeed.description ?: "",
                author: syndFeed.author,
                type: syndFeed.feedType
        )
        for (SyndEntry entry in syndFeed.entries) {
            String title = entry.title
            String description = entry.description?.value ?: ''
            if (!description) {  // mustn't be rss... could be atom
                description = entry.contents[0]?.value ?: ''
            }
            // trim to 4k-ish size for db storage
            description = description?.take(4000)
            String link = entry.link
            Date publishedDate = entry.publishedDate
            String summary = description.replaceAll("</?[^>]+>", "").take(200) ?: ''

            def feedEntry = new FeedEntry(title: title, link: link, publishedDate: publishedDate,
                    description: description,
                    summary: summary,
                    author: entry.author ?: "")

            //TODO ignore stuff older than X days
            int trimEntriesOlderThanXdays = config.feeds.ignoreFeedEntriesOlderThan
            if (trimEntriesOlderThanXdays) {
                def trimTime = new Date() - trimEntriesOlderThanXdays // X days ago
                if (publishedDate && publishedDate < trimTime) {
                    log.debug("Skipping old entry: [$title] from [$publishedDate]")
                    continue
                }
            }

            if (feedEntry) {
                if (translate) {
                    feedEntry.language = translateService.getLanguage(description)
                }
                log.trace("Read entry with title [$title] and link [$link]")
                feedInfo.entries.add(feedEntry)
            }
        }
        return feedInfo


    }

    void updateFeed(Blog blog, FeedInfo feedInfo) {

        // we iterate in reverse to ensure newest entries have the newest timestamps
        feedInfo?.entries?.reverseEach { entry ->

            String md5Hash = entry.summary.encodeAsMD5()
            def existing = BlogEntry.findByHash(md5Hash) || BlogEntry.findByLink(entry.link)
            if (!existing) {

                BlogEntry blogEntry = new BlogEntry(title: entry.title, link: entry.link,
                        description: entry.description,
                        language: entry.language,
                        hash: md5Hash,
                        sourceStatus: 200,
                        sourceStatusDate: new Date())

                if (blogEntry.groovyRelated) {
                    //log.info("Added new entry: $be.title")
                    try {
                        if (!blogEntry.validate()) {
                            log.warn("Validation failed updating blog entry [$blogEntry.title]")
                            blogEntry.errors.fieldErrors.each {
                                log.warn "${it.field}: ${it.code}"
                            }
                        } else {
                            blog.addToBlogEntries(blogEntry)
                            blogEntry.save(flush: true)
                            blog.save(flush: true)

                            if (config.twitter.enabled) {
                                twitterService.sendTweet("${blogEntry.title} -- ${blogEntry.link} -- ${blog.title}")
                            }

                            if (config.thumbnail.enabled) {
                                notify "thumbnail.request", blogEntry
                            }
                        }
                    } catch (t) {
                        log.error t.message, t
                    }

                    log.debug("Saved entry with title [$blogEntry.title]")
                } else {
                    log.trace("Ignoring non-groovy blog entry: $blogEntry.title")
                }
            }
        }
        blog.save(flush: true)
    }


    @Transactional(noRollbackFor = [FileNotFoundException, ParsingFeedException, UnknownHostException])
    void updateFeed(Blog blog) {
        try {
            refreshBlogUrl(blog)
            log.info("Now polling: [$blog]")
            FeedInfo feedInfo = getFeedInfo(blog.feedUrl, config.translate.enabled as Boolean)
            if (feedInfo) {
                updateFeed(blog, feedInfo)
                notify BLOG_MARK_SUCCESS, blog.id
                //markBlogUpdateSuccess(blog)
            } else {
                notify BLOG_MARK_ERROR, blog.id
                //markBlogWithError(blog)
            }
        } catch (Exception e) {
            log.warn("Error parsing ${blog.feedUrl}: ${e.message}")
            notify BLOG_MARK_ERROR, [blog.id, e.message]
//            markBlogWithError(blog, new DefaultStackTraceFilterer().filter(e, true))
        }
        blog.save()
    }

    protected Map getConfig() {
        grailsApplication.config
    }

    void updateFeeds() {

        log.debug("FeedService starting polled update")
        def feedsToUpdate = Blog.findAllByStatusAndNextPollLessThan(BlogStatus.ACTIVE, new Date())
        log.debug("${feedsToUpdate.size()} to update")

        // Limit to 5 updated blogs per minute. Could probably up this significantly
        // by going multithreaded...
        int maxPollsPerMinute = config.http.maxpollsperminute
//        if (feedsToUpdate.size() > maxpollsperminute) {
//            log.warn("${feedsToUpdate.size()} exceeds max for this minute. Limiting update to ${maxpollsperminute}.")
//            feedsToUpdate = feedsToUpdate[0..maxpollsperminute - 1]
//        }

        feedsToUpdate.take(maxPollsPerMinute).each { blog ->
            notify FEED_UPDATE, blog.id

//                updateFeed(blog)
        }

        log.debug("FeedService finished polled update")
    }

    def updateLists() {

        def allEntries = []

        config.lists.each { name, String url ->

            log.debug("Updating list [$name] from [$url]")
            try {

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
                log.trace "Filtered original entries from ${feed.entries.size()} to ${feedEntries.size()}"
                feedEntries.each { entry ->
                    entry.info = name
                    allEntries << entry
                }
            } catch (Exception e) {
                log.warn("Error parsing ${url}: ${e.message}", new DefaultStackTraceFilterer().filter(e, true))
            }


        }

        // sort in date desc
        allEntries = allEntries.sort { it.publishedDate }

        log.trace("Putting to list cache: ${allEntries.size()}")

        listCache.put(new Element("listEntries", allEntries))

        return allEntries
    }


    def getCachedListEntries() {
        listCache.get("listEntries")?.objectValue ?: updateLists()
    }

    def updateTweets() {
        if (!config.tweets.enable) {
            return
        }

        def tweetFeed = getFeedInfo(config.tweets.url as String, false)
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
        tweetCache.get("tweetEntries")?.objectValue ?: updateTweets()
    }

    @Transactional()
    int checkPendingBlogs(List<Blog> blogs) {

        Map<Blog, FeedInfo> feedInfos = blogs.collectEntries { Blog blog ->
            try {
                refreshBlogUrl(blog)
                [blog, getFeedInfo(blog.feedUrl)]
            } catch (e) {
                blog.status = BlogStatus.ERROR
                blog.lastError = "Error parsing [$blog.feedUrl] $e.message"
                blog.save()
                [blog, null]
            }
        }.findAll { it.value }

        feedInfos.each { Blog blog, FeedInfo feedInfo ->
            // Find if any of the blogs entries currently contains something Groovy related
            log.trace("Checking $blog.title")

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
     * Will return an unsaved Blog with BlogEntries
     * @param feedUrl
     * @return
     */
    Blog testFeed(String feedUrl) {
        try {

            def feedInfo = getFeedInfo(feedUrl)
            def blog = createBlogFromFeedInfo(feedInfo)
            feedInfo.entries.each {
                blog.addToBlogEntries(title: it.title, description: it.description)
            }
            return blog
        } catch (Exception e) {
            return null
        }

    }

    /**
     * Create a blog from the feed url
     * @param feedUrl
     * @param account
     * @return
     */
    Blog createBlog(String feedUrl, User account = null) {
        try {
            def feedInfo = getFeedInfo(feedUrl)
            return createBlogFromFeedInfo(feedInfo, account)
        } catch (Exception e) {
            return null
        }
    }

    boolean saveBlog(Blog blog) {
        blog.save(flush: true)
        if (config.feeds.moderate) {
            blog.status = BlogStatus.PENDING
            try {
                def approve = grailsLinkGenerator.link(controller: 'account', action: 'approveFeed', id: blog.id, absolute: true)
                def reject = grailsLinkGenerator.link(controller: 'account', action: 'removeFeed', id: blog.id, absolute: true)
                mailService.sendMail {
                    to config.feeds.moderator_email
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

        SyndFeed feed = feedCache.get("romeFeed-$feedType")?.objectValue

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

    private String getFinalURL(String url, int count = 0) {
        HttpURLConnection con = (HttpURLConnection) url.toURL().openConnection()
        con.setInstanceFollowRedirects(false)
        con.connect()
        con.getInputStream()
        if (count > 10) {
            log.warn("Forwarding more than 10 times. Returning $url")
            return url
        }
        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location")
            if (redirectUrl == url) {
                log.warn("Final url for $url is a redirect loop")
                return url
            }

            return getFinalURL(redirectUrl, count + 1)
        }
        return url
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

    /**
     * Follow the blogs url redirects and update if the redirects has changed.
     * @param blog
     */
    private void refreshBlogUrl(Blog blog) {
        String url = getFinalURL(blog.feedUrl)
        if (url != blog.feedUrl) {
            def oldUrl = blog.feedUrl
            blog.feedUrl = url
            if (!blog.validate(['feedUrl'])) {
                def duplicate = Blog.findByFeedUrl(url)
                throw new RuntimeException("Resolved feedUrl for $blog is a dupplicate of $duplicate. Reverting to $oldUrl")
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markBlogUpdateSuccess(Long id) {
        Blog blog = Blog.get(id)

        if (blog.status == BlogStatus.ACTIVE) return
        log.debug("FeedService marked blog $blog ACTIVE")
        blog.lastError = ''
        blog.errorCount = 0
        blog.status = BlogStatus.ACTIVE
        updateNextPollForBlog(blog)

        blog.save()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markBlogWithError(Long id, String message = null) {
        Blog blog = Blog.get(id)

        blog.lastError = message?.take(254) ?: ''
        blog.errorCount = blog.errorCount + 1
        log.warn("${blog.id}: Encountered error in [$blog]: $message.")
        log.warn("${blog.id}: -- This is error number ${blog.errorCount}")
        if (blog.errorCount > (config.groovyblogs.maxErrors ?: 10)) {
            log.warn("${blog.id}: -- Exceeded max ${config.groovyblogs.maxErrors ?: 10}. Blog marked with ERROR")
            blog.status = BlogStatus.ERROR
        }
        updateNextPollForBlog(blog)
        blog.save()
    }

    private void updateNextPollForBlog(Blog blog) {
//        Blog blog = Blog.get(id)

        blog.lastPolled = new Date()
        long nextPollTime = System.currentTimeMillis() + blog.pollFrequency * 60 * 60 * 1000
        blog.nextPoll = new Date(nextPollTime)

        log.debug("Next poll of [$blog.title] at $blog.nextPoll")
    }


    @PostConstruct
    void setupEventListener() {
        on(FEED_UPDATE) { Long id ->
            log.debug("Updating by id: $id")
            Blog.withNewSession {
                Blog blog = Blog.get(id)
                this.updateFeed(blog)
            }
        }
        on(BLOG_MARK_SUCCESS) { Long id ->
            try {
                Blog.withNewSession {
                    this.markBlogUpdateSuccess(id)
//                    this.updateNextPollForBlog(id)
                }
            } catch (e) {
                log.error(e.message, e)
            }
        }
        on(BLOG_MARK_ERROR) { Long id, String message = null ->
            try {
                Blog.withNewSession {
                    this.markBlogWithError(id, message)
//                    this.updateNextPollForBlog(id)
                }
            } catch (e) {
                log.error(e.message, e)
            }

        }

    }
}
