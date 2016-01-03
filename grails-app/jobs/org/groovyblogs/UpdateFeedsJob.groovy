package org.groovyblogs

class UpdateFeedsJob {

    FeedService feedService

    def concurrent = false

    static triggers = {
        simple name:'updateFeedJobTrigger', repeatInterval: 1000 * 60, startDelay: 1000 * 15
    }

    void execute() {

        log.debug("Starting scheduled feed check at: ${new Date()}")

        try {
            feedService.updateFeeds()
        } catch (t) {
            log.error("Error updating feeds: $t.message", t)
        }

        log.debug("Finished scheduled feed check at: ${new Date()}")
    }
}
