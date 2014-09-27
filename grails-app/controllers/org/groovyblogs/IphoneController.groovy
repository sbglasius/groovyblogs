package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class IphoneController {

    EntriesService entriesService

    static defaultAction = 'recent'

    def recent() {
        [entries: entriesService.limitEntries(entriesService.getRecentEntries())]
    }

    def show() {
        [entry: BlogEntry.get(params.id)]
    }

    def pc() {
        // For now, redirect back to the home page.
        redirect(uri: '/')
    }
}
