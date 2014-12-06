package org.groovyblogs
import grails.plugin.cache.Cacheable
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

@Transactional(readOnly = true)
class EntriesService {

    protected static final int DEFAULT_DAYS_TO_REPORT = 7

    @Cacheable('recentList')
    Collection<BlogEntry> getRecentEntries(Integer max, Long offset) {
        log.debug "getRecentEntries(max=$max, offset=$offset)"
        def entries = BlogEntry.list([sort: 'dateAdded', order: "desc", max: max, offset: offset])

        entries.findAll { it.isGroovyRelated() }
    }

    @Cacheable('popularList')
    Collection<BlogEntry> getPopularEntries(Integer days = DEFAULT_DAYS_TO_REPORT, Integer max, Long offset) {

        log.debug "getPopularEntries(days=$days, max=$max, offset=$offset)"

        def aWhileAgo = new Date() - days

        def entries = BlogEntry.findAllByDateAddedGreaterThanAndHitCountGreaterThan(aWhileAgo, 0, [sort: 'hitCount', order: "desc", max: max, offset: offset])

        entries.findAll { it.isGroovyRelated() }
    }

    @Transactional
    BlogEntry getEntry(long id) {
        def blogEntry = BlogEntry.get(id)
        if (blogEntry) {
            blogEntry.hitCount++
            blogEntry.addToVisits(new Date())
            blogEntry.save(failOnError: true)
        }
        return blogEntry
    }

    @Transactional
    void checkBlogEntrySource(BlogEntry blogEntry) {
        def newSourceStatus
        try {
            String url = blogEntry.link
            RestResponse resp = followUrl(url)
            newSourceStatus = resp.status
        } catch (e) {
            newSourceStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        if(blogEntry.sourceStatus != newSourceStatus) {
            blogEntry.sourceStatus = newSourceStatus
            blogEntry.sourceStatusDate = new Date()
            blogEntry.save(failOnError: true)
            if(!blogEntry.isSourceAvailable()) {
                log.info("Blog entry id: ${blogEntry.id} source reported as not available. Code: $newSourceStatus")
            }
        }
    }

    private RestResponse followUrl(String url) {
        def rest = new RestBuilder()
        def resp = rest.head(url)
        if(resp.statusCode in [HttpStatus.TEMPORARY_REDIRECT, HttpStatus.MOVED_PERMANENTLY] && resp.headers['Location']) {
            String redirectUrl = resp.headers['Location'].first()
            log.debug("Follow redirect from $url to $redirectUrl")
            resp = followUrl(redirectUrl)
        }
        resp
    }

    @Transactional
    void verifyBlogsEntrySources() {
        def blogEntries = BlogEntry.list(max: 25, sort: 'sourceStatusDate', order: 'asc')
        assert blogEntries.size() <= 25
        blogEntries.each {
            try {
                log.debug("Checking source of blog entry $it")
                checkBlogEntrySource(it)
            } catch (e) {
                log.warn("Failed to verify blog entry $it.",e)
            }
        }

    }
}
