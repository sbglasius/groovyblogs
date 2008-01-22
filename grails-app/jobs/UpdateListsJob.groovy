import org.apache.commons.logging.LogFactory

class UpdateListsJob {

	//def log = LogFactory.getLog(UpdateListsJob.class.name)
	
	FeedService feedService
	
	// Update the lists every 15 minutes
	def cronExpression = "15 0,15,30,45 * * * ?"
			

	def execute() {	
		
		log.info("Starting scheduled lists check at: " + new Date())
	
		feedService.updateLists()
	    
	    log.info("Finished scheduled lists check at: " + new Date())
	}
}
