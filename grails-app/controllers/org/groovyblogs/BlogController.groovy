package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class BlogController {

    private static final List STATUSES = [BlogStatus.PENDING, BlogStatus.NO_GROOVY, BlogStatus.LOOKS_BAD, BlogStatus.ERROR]

    def feedService
    static scaffold = Blog

    def checkPendingBlogs() {
        def blogs = params.list('blog.id').collect { Blog.get(it) }.findAll { it.status in STATUSES }
        def count = feedService.checkPendingBlogs(blogs)
        flash.message = "Checked ${count} blogs"
        redirect action: 'index',
                 params: [max: params.max ?: 10, offset: params.offset ?: 0,
                          sort: params.sort ?: 'id', order: params.order ?: 'asc']
    }

    def checkBlogNow(Blog blog) {
        if (!blog) {
            flash.message = "Blog not found: $params.id"
            redirect(action: 'index')
            return
        }

        feedService.updateFeed(blog)
        flash.message = "Checked $blog.title"
        redirect(action: 'show', id: blog.id)
    }
}
