
class UpdateThumbsJob {

	CacheService cacheService
	ThumbnailService thumbnailService

	def startDelay = 1000 * 90  // wait 90 seconds
    def timeout = 1000 * 60 * 5 // run every 5 minutes
	
	// Update the lists every 15 minutes, 1,16,31,46
	// def cronExpression = "30 * * * * ?"
					

	def execute() {	
		
		log.info("Starting thumbnail updates check at: " + new Date())
	
		def aWhileAgo = new Date().minus(7) // 7 days ago

		def entries = BlogEntry.findAllByDateAddedGreaterThan(
						aWhileAgo, [ sort: 'dateAdded', order: "desc" ] )
		entries = EntriesController.limitEntries(entries)
		entries = entries.findAll { entry -> entry.isGroovyRelated() }
		entries.each { entry ->
		    log.info("Checking thumbnail for (${entry.id}) at ${entry.toThumbnailPath()}")
            thumbnailService.getFile("${entry.id}", true, true)
		}
	    
	    log.info("Finished thumbnail updates check at: " + new Date())
	}
}
