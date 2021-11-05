package org.groovyblogs

class UpdateListsJob {

    FeedService feedService

    def concurrent = false

    static triggers = {
        cron name: 'updateListJobTrigger', startDelay:10000, cronExpression:  "0 0,15,30,45 * * * ?"
    }

    void execute() {

        log.info("Starting scheduled lists check at: ${new Date()}")

        feedService.updateLists()

        log.info("Finished scheduled lists check at: ${new Date()}")
    }
}
