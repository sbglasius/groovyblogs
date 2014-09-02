package org.groovyblogs

class UpdateListsJob {

    FeedService feedService

    def concurrent = false

    // Update the lists every 15 minutes
    def cronExpression = "0 0,15,30,45 * * * ?"


    def execute() {

        log.info("Starting scheduled lists check at: " + new Date())

        feedService.updateLists()

        log.info("Finished scheduled lists check at: " + new Date())
    }
}
