package org.groovyblogs

import org.springframework.beans.factory.annotation.Value

class UpdateTweetsJob {

    @Value('${tweets.enable:false}')
    boolean tweetsEnabled

    FeedService feedService

    def concurrent = false

    // Update the tweets every 15 minutes
    static triggers = {
        cron name:'updateTweetsJobTrigger', startDelay:10000, cronExpression:  "0 0,15,30,45 * * * ?"
    }

    void execute() {
        if (!tweetsEnabled) {
            return
        }
        log.info("Starting scheduled tweets check at: ${new Date()}")

        feedService.updateTweets()

        log.info("Finished scheduled tweets check at: ${new Date()}")
    }
}
