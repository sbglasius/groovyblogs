
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class UpdateThumbsJob {

    def concurrent = false

    CacheService cacheService
	ThumbnailService thumbnailService

	def startDelay = 1000 * 60 * 30  // wait 30 mins
    def timeout = 1000 * 60 * 60 // run every hour
	
	// Update the lists every 15 minutes, 1,16,31,46
	// def cronExpression = "30 * * * * ?"
					

	def execute() {

        if (!ConfigurationHolder.config.thumbnail.enabled) {
            // service disabled
            return
        }

        log.info("Starting thumbnail updates check at: " + new Date())
        try {

            def aWhileAgo = new Date().minus(7) // 7 days ago

            def entries = BlogEntry.findAllByDateAddedGreaterThan(
                    aWhileAgo, [sort: 'dateAdded', order: "desc"])
            entries = EntriesController.limitEntries(entries)
            entries = entries.findAll {entry -> entry.isGroovyRelated() }
            entries.each {entry ->
                log.info("Checking thumbnail for (${entry.id}) at ${entry.toThumbnailPath()}")
                thumbnailService.getFile("${entry.id}", true, true)
            }

        } catch (Throwable t) {
            log.error("Error in thumbnail check", t)
        }

        log.info("Finished thumbnail updates check at: " + new Date())
	}
}
