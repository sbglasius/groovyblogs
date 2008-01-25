import org.apache.commons.logging.LogFactory

class UpdateFeedsJob {
	
	//def log = LogFactory.getLog(UpdateFeedsJob.class.name)

	FeedService feedService
	
	// def cronExpression = "0 * * * * ?"
	// use default to fetch every minute		

	def execute() {	
		
		log.info("Starting scheduled feed check at: " + new Date())
	
		feedService.updateFeeds()
	    
	    log.info("Finished scheduled feed check at: " + new Date())
	}
}
