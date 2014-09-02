package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class IphoneController {

    EntriesService entriesService

    def index() { redirect(action: 'recent', params: params) }

    // suppress when more than three entries from same author
    public static def limitEntries(entries) {

        def authorHash = [:] // count by blog
        def limitEntries = [] // limit to three entries

        entries.each { entry ->
            def key = entry.blog.feedUrl
            int entryCount = authorHash[key] ? authorHash[key] : 0
            entryCount++
            authorHash[key] = entryCount
            if (entryCount <= 3) {
                if (entryCount == 3)
                    entry.info = "Reached limit of 3 displayed entries shown for ${entry.blog.title}. " +
                            "<a href='../blog/show/${entry.blog.id}'>Read more...</a>"
                limitEntries << entry
            }
        }
        return limitEntries

    }


    def recent() {

        def entries = entriesService.getRecentEntries()

        return [entries: entriesService.limitEntries(entries)]
    }

    def show() {

        [entry: BlogEntry.get(params.id)]

    }

    def pc() {
        // For now, redirect back to the home page.
        redirect(uri: '/')
    }
}
