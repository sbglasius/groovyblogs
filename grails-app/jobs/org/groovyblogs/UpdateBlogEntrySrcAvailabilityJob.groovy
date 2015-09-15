package org.groovyblogs


class UpdateBlogEntrySrcAvailabilityJob {
    def entriesService

    static triggers = {
        simple name:'updateBlogEntrySrcAvailabilityJobTrigger', repeatInterval: 1000 * 60, startDelay: 10500 * 15
    }

    def execute() {
        log.info("Starting scheduled blog entries source checker at: ${new Date()}")

        entriesService.verifyBlogsEntrySources()

        log.info("Finished scheduled blog entries source checker at: ${new Date()}")

    }
}
