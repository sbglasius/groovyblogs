package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured
import net.sf.ehcache.Element

import com.sun.syndication.feed.synd.SyndContentImpl
import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.feed.synd.SyndFeedImpl
import com.sun.syndication.io.SyndFeedOutput

@Secured(['permitAll'])
class FeedController {

    def feedCache

    static final supportedFormats = ["rss_0.90", "rss_0.91", "rss_0.92", "rss_0.93", "rss_0.94", "rss_1.0", "rss_2.0", "atom_0.3", "atom_1.0"]

    static defaultAction = 'atom'

    private boolean useFeedburner() {
        if (!grailsApplication.config.http.usefeedburner) {
            return false
        }

        def userAgent = request.getHeader("user-agent")
        if (userAgent && userAgent =~ /(?i)FeedBurner/) {
            log.info("Feedburner Agent Detected: [$userAgent]")
            return false
        }

        def remoteAddr = request.getRemoteAddr()
        log.debug("Redirecting: [$userAgent] from [$remoteAddr] to feedburner")
        return true
    }

    def rss() {
        if (useFeedburner()) {
            response.sendRedirect(grailsApplication.config.http.feedburner_rss)
        } else {
            render(text: getFeed("rss_2.0"), contentType: "text/xml", encoding: "UTF-8")
        }
    }

    def atom() {
        if (useFeedburner()) {
            response.sendRedirect(grailsApplication.config.http.feedburner_atom)
        } else {
            render(text: getFeed("atom_1.0"), contentType: "text/xml", encoding: "UTF-8")
        }
    }

    // or specify your own feed type
    def all() {
        def format = params.id
        if (supportedFormats.contains(format)) {
            render(text: getFeed(format), contentType: "text/xml", encoding: "UTF-8")
        } else {
            response.sendError(response.SC_FORBIDDEN)
        }
    }

    private String getFeed(feedType) {

        SyndFeed feed = feedCache.get("romeFeed-$feedType")?.value

        if (!feed) {
            // def blogEntries = BlogEntry.listOrderByDateAdded(max: 30, order: "desc")
            def aWhileAgo = new Date() - 7 // 7 days ago

            def blogEntries = BlogEntry.findAllByDateAddedGreaterThan(
                    aWhileAgo, [sort: 'dateAdded', order: "desc"])

            blogEntries = blogEntries.findAll { it.isGroovyRelated() }

            def feedEntries = []
            blogEntries.each { blogEntry ->
                def desc = new SyndContentImpl(type: "text/plain", value: FeedEntry.summarize(blogEntry.description))
                def entry = new SyndEntryImpl(title: blogEntry.title,
                        link: 'http://www.groovyblogs.org/entries/jump?id=' + blogEntry.id,
                        publishedDate: blogEntry.dateAdded, description: desc, author: blogEntry.blog.title)
                feedEntries.add(entry)
            }
            feed = new SyndFeedImpl(feedType: feedType, title: 'GroovyBlogs.org',
                    link: 'http://www.groovyblogs.org', description: 'groovyblogs.org Recent Entries',
                    entries: feedEntries)

            feedCache.put(new Element("romeFeed-" + feedType, feed))
        }

        StringWriter writer = new StringWriter()
        SyndFeedOutput output = new SyndFeedOutput()
        output.output(feed, writer)
        writer.close()

        writer
    }
}

