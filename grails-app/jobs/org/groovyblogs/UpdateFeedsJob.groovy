package org.groovyblogs

class UpdateFeedsJob {

    FeedService feedService

    def concurrent = false
    static triggers = {
        simple name:'updateFeedJobTrigger', repeatInterval: 1000 * 60, startDelay: 1000 * 15
    }


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
