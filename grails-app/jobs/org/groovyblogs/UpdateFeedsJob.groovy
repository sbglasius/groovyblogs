package org.groovyblogs

import grails.gorm.transactions.Transactional

class UpdateFeedsJob {

    FeedService feedService
    def concurrent = false

    static triggers = {
        simple name: 'updateFeedJobTrigger', repeatInterval: 1000 * 30, startDelay: 1000 * 15
    }

    void execute() {
        log.info("Starting scheduled feed check at: ${new Date()}")

        try {
            feedService.updateFeeds()
            log.info("Finished scheduled feed check at: ${new Date()}")
        } catch (t) {
            
            log.error("Error updating feeds: $t.message")
        }
    }
}
