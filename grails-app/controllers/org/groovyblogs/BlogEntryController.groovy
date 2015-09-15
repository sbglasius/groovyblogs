package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class BlogEntryController {
    static scaffold = true
    def entriesService

    def toggleDisableFlag(BlogEntry blogEntry) {
        log.debug("toggleDisableFlag: $blogEntry")
        if(blogEntry) {
            entriesService.toggleDisableFlag(blogEntry)
        }
        redirect(controller: 'entries')
    }

}
