package org.groovyblogs

import grails.plugin.cache.Cacheable
import grails.transaction.Transactional
import net.sf.ehcache.Element

@Transactional(readOnly = true)
class EntriesService {

    protected static final int DEFAULT_DAYS_TO_REPORT = 7

    def entriesCache

    // suppress when more than three entries from same author
    def limitEntries(entries) {

        def authorHash = [:] // count by blog
        def limitEntries = [] // limit to three entries

        entries.each { entry ->
            def key = entry.blog.feedUrl
            int entryCount = authorHash[key] ?: 0
            entryCount++
            authorHash[key] = entryCount
            if (entryCount <= 3) {
                if (entryCount == 3)
                    entry.info = "Reached limit of 3 displayed entries shown for ${entry.blog.title}. " +
                            "<a href='../blog/show/${entry.blog.id}'>Read more...</a>"
                limitEntries << entry
            }
        }
        return limitEntries
    }

    def getEndlessEntries(params) {

        def entries = entriesCache.get("endlessList-${params.offset}")?.value
        if (!entries) {
            params.order = "desc"
            entries = BlogEntry.listOrderByDateAdded(params)
            entries = entries.findAll { it.isGroovyRelated() }
            entriesCache.put(new Element("endlessList=${params.offset}", entries))
        }
        return entries
    }

    @Cacheable('recentList')
    def getRecentEntries(int days = DEFAULT_DAYS_TO_REPORT) {
        log.debug "Recent cache for recent ${days} days not available. Reading from db"

        def aWhileAgo = new Date() - days // 7 days ago is the default

        BlogEntry.findAllByDateAddedGreaterThan(aWhileAgo, [sort: 'dateAdded', order: "desc"]).findAll { it.isGroovyRelated() }
    }

    @Cacheable('popularList')
    def getPopularEntries() {

        log.debug "Popular cache for popularList not available. Reading from db."

        def aWhileAgo = new Date() - DEFAULT_DAYS_TO_REPORT // 7 days ago

        def entries = BlogEntry.findAllByDateAddedGreaterThanAndHitCountGreaterThan(aWhileAgo, 0, [sort: 'hitCount', order: "desc"])
        entries = entries.findAll { it.isGroovyRelated() }

        entriesCache.put(new Element("popularList", entries))
        return entries
    }

    @Transactional()
    BlogEntry getEntry(long id) {
        def blogEntry = BlogEntry.get(id)
        if (blogEntry) {
            blogEntry.hitCount++
            blogEntry.save(failOnError: true)
        }
        return blogEntry
    }
}
