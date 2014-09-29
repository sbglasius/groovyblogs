package org.groovyblogs

import net.sf.ehcache.Ehcache

class UpdateThumbsJob {

    def concurrent = false

    def grailsApplication
    ThumbnailService thumbnailService
    Ehcache pendingCache

    static triggers = {
        simple name:'updateThumbsJobTrigger', repeatInterval: 1000 * 60, startDelay: 1000 * 30
    }

    void execute() {

        if (!grailsApplication.config.thumbnail.enabled) {
            // service disabled
            return
        }

        log.info("Starting thumbnail updates check at: ${new Date()}")
        try {
            def urlsToFetch = pendingCache.getKeys()
            if (urlsToFetch) {
                log.info "Need to update ${urlsToFetch.size()} thumbnails"
            }

            urlsToFetch.each { url ->
                try {
                    def value = pendingCache.get(url)?.value
                    if (value) {
                        long id = value
                        log.info "Refreshing pending cache for ${url} on blog ${id}"
                        thumbnailService.fetchThumbnailsToCache(id, url)
                    }
                    else {
                        pendingCache.remove(url)
                    }
                }
                catch (e) {
                    pendingCache.remove(url)
                }
            }
        } catch (t) {
            log.error("Error in thumbnail check: $t.message", t)
        }

        pendingCache.removeAll()
        log.info("Finished thumbnail updates check at: ${new Date()}")
    }
}
