package org.groovyblogs

import grails.util.Holders
import net.sf.ehcache.Ehcache

class UpdateThumbsJob {

    def concurrent = false

    ThumbnailService thumbnailService
    Ehcache pendingCache

    static triggers = {
        simple name:'updateThumbsJobTrigger', repeatInterval: 1000 * 60, startDelay: 1000 * 30
    }


    def execute() {

        if (!Holders.config.thumbnail.enabled) {
            // service disabled
            return
        }


        log.info("Starting thumbnail updates check at: " + new Date())
        try {
            def urlsToFetch = pendingCache.getKeys()
            if (urlsToFetch) {
                log.info "Need to update ${urlsToFetch.size()} thumbnails"
            }

            urlsToFetch.each { url ->
                try {
                    def value = pendingCache.get(url)?.value
                    if(!value) {
                        pendingCache.remove(url)
                    } else {
                        long id = value
                        log.info "Refreshing pending cache for ${url} on blog ${id}"
                        //Thread.start {
                        thumbnailService.fetchThumbnailsToCache(id, url)
                    }

                    // pendingCache.remove(url)
                    //}
                } catch (exception) {
                    pendingCache.remove(url)
                }
            }


        } catch (Throwable t) {
            log.error("Error in thumbnail check", t)
        }

        pendingCache.removeAll()
        log.info("Finished thumbnail updates check at: " + new Date())
    }
}
