package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class UserController {

    static scaffold = true

    def userService

    def cleanupUsers() {
        def users = User.list(params).findAll { !it.blogs?.size() }
        def count = userService.bulkDeleteUsers(users)
        flash.message = "Removed ${count} users"
        redirect(action: 'index', params: params)
    }

    def emailAboutNewGroovyblogs() {
        def usersWithActiveBlogs = User.list().findAll { it.blogs?.any {it.status == BlogStatus.ACTIVE}}

        userService.emailAoutNewGroovyBlogs(usersWithActiveBlogs)
        redirect(action: 'index')

    }
}
