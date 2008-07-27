
class UpdateFeedsJob {
	
	FeedService feedService

    def concurrent = false

    private static updating = false

    def startDelay = 1000 * 60 * 1  // wait 1 mins

    // def cronExpression = "0 * * * * ?"
	// use default to fetch every minute		

	def execute() {

        if (!updating) {

            updating = true

            log.info("Starting scheduled feed check at: " + new Date())

            try {
                feedService.updateFeeds()
            } catch (Throwable t) {
                log.error("Error updating feeds", t)
            }

            updating = false

            log.info("Finished scheduled feed check at: " + new Date())

        } else {

            log.warn("\n\n>>>>Skipping Feed update. Previous job in progress\n\n")

        }
    }
}
