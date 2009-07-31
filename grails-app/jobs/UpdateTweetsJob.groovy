
class UpdateTweetsJob {
	
    FeedService feedService
	
    // Update the tweets every 15 minutes
    def cronExpression = "15 0,15,30,45 * * * ?"
			

    def execute() {
		
        log.info("Starting scheduled tweets check at: " + new Date())
	
        feedService.updateTweets()
	    
        log.info("Finished scheduled tweets check at: " + new Date())
    }
}
