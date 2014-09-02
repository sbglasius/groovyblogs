package org.groovyblogs

import grails.util.Holders
import net.sf.ehcache.Element
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod

class FeedService {

    def listCache
    def pendingCache
    def tweetCache
    ThumbnailService thumbnailService
    TranslateService translateService
    TwitterService twitterService


    // Returns the HTML for the supplied URL
    String getHtmlForUrl(url) {

        log.info("Trying to fetch [$url]")

        def client = new HttpClient()
        def clientParams = client.getParams()
        clientParams.setParameter(org.apache.commons.httpclient.params.HttpClientParams.HTTP_CONTENT_CHARSET, "UTF-8");

        if (Holders.config.http.useproxy) {
            def hostConfig = client.getHostConfiguration()
            hostConfig.setProxy(Holders.config.http.host, Holders.config.http.port as int)
            log.warn("Setting proxy to [" + Holders.config.http.host + "]")
        }

        if (Holders.config.http.useragent) {
            clientParams.setParameter(org.apache.commons.httpclient.params.HttpClientParams.USER_AGENT,
                Holders.config.http.useragent)
        }

        if (Holders.config.http.timeout) {

            clientParams.setParameter(org.apache.commons.httpclient.params.HttpClientParams.SO_TIMEOUT,
                Holders.config.http.timeout)
        }

        def mthd = new GetMethod(url)

        def statusCode = client.executeMethod(mthd)
        def responseBody = mthd.getResponseBody()
        mthd.releaseConnection()

        def urlStr = new String(responseBody)

        log.debug("Fetched [$url] successfully")

        return urlStr

    }


    // takes html and returns a org.groovyblogs.FeedInfo object
    def getFeedInfoFromHtml(feedStr, translate) {

        def sfi = new com.sun.syndication.io.SyndFeedInput()
        // def feedReader = new StringReader(feedStr)

        // need this to handle UTF8 encoding correctly
        def bais = new ByteArrayInputStream(feedStr.getBytes("UTF-8"))
        def feedReader = new com.sun.syndication.io.XmlReader(bais)

        def sf = sfi.build(feedReader)


        def feedInfo = new FeedInfo(title: sf.title,
            description: sf.description ? sf.description : "",
            author: sf.author, type: sf.feedType)

        for (e in sf.entries) {
            String title = e.title
            String description = e.description?.value
            if (!description) {  // mustn't be rss... could be atom
                description = e.contents[0]?.value
            }
            // trim to 4k-ish size for db storage
            if (description.length() > 4000) {
                description = description[0..3999]
            }
            String link = e.link
            Date publishedDate = e.publishedDate
            def summary
            if (description) {
                // strip html for the summary, then truncate
                summary = description.replaceAll("</?[^>]+>", "")
                summary = summary.length() > 200 ? summary[0..199] : summary
            }

            def feedEntry = new FeedEntry(title: title, link: link, publishedDate: publishedDate,
                description: description ? description : "",
                summary: summary ? summary : "",
                author: e.author ? e.author : "")

            //TODO ignore stuff older than X days
            def trimEntriesOlderThanXdays = Holders.config.feeds.ignoreFeedEntriesOlderThan
            if (trimEntriesOlderThanXdays) {
                def trimTime = new Date().minus(trimEntriesOlderThanXdays) // X days ago
                if (publishedDate && publishedDate < trimTime) {
                    log.debug("Skipping old entry: [$title] from [$publishedDate]")
                    feedEntry = null // too old to include
                }
            }



            if (feedEntry) {
                if (translate)
                feedEntry.language = translateService.getLanguage(description)
                log.debug("Found entry with title [$title] and link [$link]")
                feedInfo.entries.add(feedEntry)
            }
        }

        // return "Author $sf.author Title $sf.title Desc $sf.description Feedtype $sf.feedType"
        return feedInfo

    }



    // takes a URL and returns ROME feed info
    def getFeedInfo(feedUrlStr, boolean translate = false) {

        def feedStr = getHtmlForUrl(feedUrlStr)
        return getFeedInfoFromHtml(feedStr, translate)


    }


    void updateFeed(Blog blog, FeedInfo fi) {

        //def existingEntries = blog.blogEntries

        // we iterate in reverse to ensure newest entries have the newest timestamps
        fi?.entries?.reverseEach {entry ->

            log.debug("Looking for $entry.link")
            //def existing = existingEntries.find { entry.link == it.link }
            def existing = BlogEntry.findByHash(entry.summary.encodeAsMD5().toString()) || BlogEntry.findByLink(entry.link)
            log.debug("Existing? " + existing)
            ///if (!BlogEntry.findByLink(entry.link)) {
            if (!existing) {

                //log.debug("Creating entry with title [$entry.title] and link [$entry.link]")

                BlogEntry be = new BlogEntry(title: entry.title, link: entry.link,
                    description: entry.description,
                    summary: entry.summary, language: entry.language,
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
	
                                if (Holders.config.twitter.enabled) {
                                    twitterService.sendTweet("${be.title} -- ${be.link} -- ${blog.title}")
                                }

                                if (Holders.config.thumbnail.enabled) {
                                    // be.thumbnail = thumbnailService.fetchThumbnail(be.link)
                                    // log.debug "Adding to pending thumbs cache: ${be?.link}"
                                    //pendingCache.put( new Element(be.link, be.id))
                                }
                            } catch (Exception e) {
                                log.debug "Error during thumbnail collection", e

                            }
							
                        }

                    } catch (Throwable t) {
                        t.printStackTrace()
                    }

                    log.debug("Saved entry with title [$be.title]")




                } else {
                    log.debug("Ignoring non-groovy blog entry: $be.title")
                }
            }

        }
        blog.lastPolled = new Date()
        long nextPollTime = new Date().getTime() + blog.pollFrequency * 60 * 60 * 1000
        blog.nextPoll = new Date(nextPollTime)
        if (!blog.validate()) {
            log.warn("Validation failed updating blog [$blog.title]")
            blog.errors.allErrors.each {
                log.warn(it)
            }
        } else {
            log.debug("Updated poll time for blog: " + blog.save(flush: true))
        }
        log.debug("Next poll of [$blog.title] at $blog.nextPoll")

    }

    void updateFeed(Blog blog) {

        log.info("Now polling: [$blog.title]")
        FeedInfo fi
        try {
            fi = getFeedInfo(blog.feedUrl, Holders.config.translate.enabled)
        } catch (Exception e) {
            log.warn("Could not parse feed [$blog.feedUrl]", e)
            blog.lastError = "Error parsing [$blog.feedUrl] " + e.message
        }
        updateFeed(blog, fi)


    }

    void updateFeedFromHtml(String blogId, String feedHtml) {

        Blog blog = Blog.get(blogId)
        log.info("Now updating: [$blog.title]")
        FeedInfo fi
        try {
            fi = getFeedInfoFromHtml(feedHtml)
        } catch (Exception e) {
            log.warn("Could not parse feed [$blog.feedUrl]", e)
            blog.lastError = "Error parsing [$blog.feedUrl] " + e.message
        }
        updateFeed(blog, fi)

    }

    void updateFeeds() {

        log.info("org.groovyblogs.FeedService starting polled update")
        def feedsToUpdate = Blog.findAllByStatusAndNextPollLessThan("ACTIVE", new Date())
        log.info("${feedsToUpdate.size()} to update")

        // Limit to 5 updated blogs per minute. Could probably up this significantly
        // by going multithreaded...
        if (feedsToUpdate.size() > Holders.config.http.maxpollsperminute) {
            log.warn("${feedsToUpdate.size()} exceeds max for this minute. Limiting update to ${Holders.config.http.maxpollsperminute}.")
            feedsToUpdate = feedsToUpdate[0..Holders.config.http.maxpollsperminute - 1]
        }

        feedsToUpdate.each {blog ->
            updateFeed(blog)
        }



        log.info("org.groovyblogs.FeedService finished polled update")
    }


    def updateLists() {

        def allEntries = []

        Holders.config.lists.each {name, url ->

            log.info("Updating list [$name] from [$url]")
            def feed = getFeedInfo(url, false)

            Blog listBlog = new Blog(title: feed.title)

            def filter = new Date().minus(1) // 1 days ago

            // Add 8 hours from Nabble feed time...
            def rightDates = feed.entries.collect {entry ->
                def diff = entry.publishedDate.time + 1000 * 60 * 60 * 7
                entry.publishedDate = new Date(diff)
                return entry
            }
            def feedEntries = rightDates.findAll {entry -> entry.publishedDate.after(filter) }
            log.info("Filtered original entries from " + feed.entries.size() + " to " + feedEntries.size())
            feedEntries.each {entry ->

                entry.info = name
                allEntries << entry

            }
        }

        // sort in date desc
        allEntries = allEntries.sort {e1, e2 ->

            if (e1.publishedDate == e2.publishedDate) {
                return 0
            } else {
                return e1.publishedDate > e2.publishedDate ? -1 : 1
            }
        }

        log.debug("Putting to list cache: " + allEntries.size())

        listCache.put(new Element("listEntries", allEntries))

        return allEntries

    }

    def getCachedListEntries() {

        def listEntries = listCache.get("listEntries")?.value
        if (!listEntries) {
            listEntries = updateLists()
        }
        return listEntries

    }



    def updateTweets() {

        def tweetFeed = getFeedInfo(Holders.config.tweets.url, false)

        def allEntries = tweetFeed.entries.collect { entry ->
            // entry.description = entry.description.replaceFirst("[^:]+:\\s*", "")
            entry
        }


        log.debug("Putting to tweet cache: " + allEntries.size())

        tweetCache.put(new Element("tweetEntries", allEntries))

        return allEntries

    }



    def getCachedTweetEntries() {

        def tweetEntries = tweetCache.get("tweetEntries")?.value
        if (!tweetEntries) {
            tweetEntries = updateTweets()
        }
        return tweetEntries


    }

}

