import grails.util.Holders
import net.sf.ehcache.Ehcache

class UpdateThumbsJob {

    def concurrent = false

    ThumbnailService thumbnailService
    Ehcache pendingCache

    def startDelay = 1000 * 30 // wait 30 secs
    def timeout = 1000 * 60 // update every minute
	
    // Update the lists every 15 minutes, 1,16,31,46
    // def cronExpression = "30 * * * * ?"
					

    def execute() {

        if (!Holders.config.thumbnail.enabled) {
            // service disabled
            return
        }


        log.info("Starting thumbnail updates check at: " + new Date())
        try {
            def urlsToFetch = pendingCache.getKeys()
            if (urlsToFetch) { log.info "Need to update ${urlsToFetch.size()} thumbnails" }

            urlsToFetch.each { url ->
                long id = pendingCache.get(url)?.value
                log.info "Refreshing pending cache for ${url} on blog ${id}"
                //Thread.start {
                    thumbnailService.fetchThumbnailsToCache(id, url)
                    
                    // pendingCache.remove(url)
                //}
            }
                       
       

        } catch (Throwable t) {
            log.error("Error in thumbnail check", t)
        }

		pendingCache.removeAll()
        log.info("Finished thumbnail updates check at: " + new Date())
    }
}
