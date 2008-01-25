import org.apache.commons.logging.LogFactory

class UpdateThumbsJob {

	//def log = LogFactory.getLog(UpdateListsJob.class.name)
	
	CacheService cacheService
	ThumbnailService thumbnailService
	
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
