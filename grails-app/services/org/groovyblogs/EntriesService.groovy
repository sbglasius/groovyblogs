package org.groovyblogs

import grails.plugin.cache.Cacheable
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.gorm.transactions.Transactional
import org.groovyblogs.data.BlogEntryDataService
import org.springframework.cache.CacheManager

import javax.servlet.http.HttpServletResponse

@Transactional(readOnly = true)
class EntriesService {

    CacheManager grailsCacheManager
    protected static final int DEFAULT_DAYS_TO_REPORT = 7

    BlogEntryDataService blogEntryDataService

    @Cacheable('recentList')
    Collection<BlogEntry> getRecentEntries(Integer max, Long offset) {
        log.debug "getRecentEntries(max=$max, offset=$offset)"

        def entries = blogEntryDataService.list(sort: 'dateAdded', order: "desc", max: max, offset: offset)

        entries.findAll { it.groovyRelated }
    }

    @Cacheable('popularList')
    Collection<BlogEntry> getPopularEntries(Integer days = DEFAULT_DAYS_TO_REPORT, Integer max, Long offset) {

        log.debug "getPopularEntries(days=$days, max=$max, offset=$offset)"

        Date aWhileAgo = new Date() - days

        def entries = blogEntryDataService.listAfterDateWithHitCount(aWhileAgo, sort: 'hitCount', order: "desc", max: max, offset: offset)

        entries.findAll { it.groovyRelated }
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
    void checkBlogEntrySource(Long id) {
        def blogEntry = BlogEntry.get(id)
        def newSourceStatus
        try {
            String url = blogEntry.link
            RestResponse resp = followUrl(url)
            newSourceStatus = resp.status
        } catch (e) {
            newSourceStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        if (blogEntry.sourceStatus != newSourceStatus) {
            blogEntry.sourceStatus = newSourceStatus
        }
        blogEntry.sourceStatusDate = new Date()
        blogEntry.save(failOnError: true, flush: true)
        if (!blogEntry.isSourceAvailable()) {
            log.info("Blog entry id: ${blogEntry.id} source reported as not available. Code: $newSourceStatus")
        }
    }

    private RestResponse followUrl(String url) {
        def rest = new RestBuilder()
        def resp = rest.head(url)
        if (resp.status in [HttpServletResponse.SC_TEMPORARY_REDIRECT, HttpServletResponse.SC_MOVED_PERMANENTLY, HttpServletResponse.SC_MOVED_TEMPORARILY] && resp.headers['Location']) {
            String redirectUrl = resp.headers['Location'].first()
            resp = followUrl(redirectUrl)
        }
        resp
    }

    @Transactional
    void verifyBlogsEntrySources() {
        def blogEntries = BlogEntry.list(max: 25, sort: 'sourceStatusDate', order: 'asc')
        assert blogEntries.size() <= 25
        blogEntries.each { blogEntry ->
            try {
                log.debug("Checking source of blog entry $blogEntry")
                checkBlogEntrySource(blogEntry.id)
            } catch (e) {
                log.warn("Failed to verify blog entry $blogEntry.", e)
            }
        }

    }

    @Transactional
    void toggleDisableFlag(BlogEntry blogEntry) {
        grailsCacheManager.getCache('recentList').clear()
        grailsCacheManager.getCache('popularList').clear()

        def nextState = !blogEntry.disabled
        blogEntry.disabled = nextState
        log.debug("Blog-entry $blogEntry has been ${nextState ? 'disabled':'enabled'}")
        blogEntry.save(failOnError: true, flush: true)
    }
}
