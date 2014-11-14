package org.groovyblogs
import grails.plugin.cache.Cacheable
import grails.transaction.Transactional

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

    @Transactional()
    BlogEntry getEntry(long id) {
        def blogEntry = BlogEntry.get(id)
        if (blogEntry) {
            blogEntry.hitCount++
            blogEntry.addToVisits(new Date())
            blogEntry.save(failOnError: true)
        }
        return blogEntry
    }
}
