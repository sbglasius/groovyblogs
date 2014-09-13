package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class BlogController {
    def feedService
    static scaffold = true

    def checkPendingBlogs() {
        def blogs = params.list('blog.id').collect { Blog.get(it) }.findAll { it.status in [BlogStatus.PENDING, BlogStatus.NO_GROOVY, BlogStatus.LOOKS_BAD, BlogStatus.ERROR] }
        def count = feedService.checkPendingBlogs(blogs)
        flash.message = "Checked ${blogs.size()} blogs"
        redirect(action: 'index', params: [max: params.max ?: 10, offset: params.offset ?: 0, sort: params.sort ?: 'id', order: params.order ?: 'asc'])
    }
}

