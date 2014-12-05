package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class BlogEntryController {
    static scaffold = true
    def entriesService

    def checkBlog(BlogEntry blogEntry) {
        entriesService.checkBlogEntrySource(blogEntry)
        render('ok')
    }
}
