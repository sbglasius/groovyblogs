package org.groovyblogs

import grails.core.GrailsApplication

class UpdateTweetsJob {

    FeedService feedService
    GrailsApplication grailsApplication

    def concurrent = false

    // Update the tweets every 15 minutes
    static triggers = {
        cron name:'updateTweetsJobTrigger', startDelay:10000, cronExpression:  "0 0,15,30,45 * * * ?"
    }

    void execute() {
        if (!grailsApplication.config.tweets.enable) {
            return
        }
        log.info("Starting scheduled tweets check at: ${new Date()}")

        feedService.updateTweets()

        log.info("Finished scheduled tweets check at: ${new Date()}")
    }
}
