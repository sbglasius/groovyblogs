import org.apache.commons.logging.LogFactory

/* Once a day update the indexes to make sure all is compressed and running well */


class UpdateIndexJob {
	
	//def log = LogFactory.getLog(UpdateIndexJob.class.name)
	
	def cronExpression = "0 0 12 * * ?"

	SearchService searchService
	
	def execute() {	
		log.info("Starting Index update at " + new Date())
		searchService.deleteIndex()
		log.info("Deleted existing index, querying DB for entries")
     	def allEntries = BlogEntry.list()
     	log.info("Indexing ${allEntries.size()} entries")
     	searchService.indexAll(allEntries)
     	log.info("Finished Reindex, Optimizing")
     	searchService.optimise()
     	log.info("Optimise complete at " + new Date())
	}
}
