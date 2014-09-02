package org.groovyblogs

class UpdateFeedsJob {

    FeedService feedService

    def concurrent = false

    def startDelay = 1000 * 60 * 1  // wait 1 mins
    def timeout = 1000 * 60 // update every minute

    def execute() {


        log.info("Starting scheduled feed check at: " + new Date())

        try {
            feedService.updateFeeds()
        } catch (Throwable t) {
            log.error("Error updating feeds", t)
        }

        log.info("Finished scheduled feed check at: " + new Date())

    }
}
