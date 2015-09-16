package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional


@SuppressWarnings(["GrUnresolvedAccess", "GroovyAssignabilityCheck"])
@Secured(['ROLE_ADMIN'])
@Transactional(readOnly = true)
class UserController {
    def userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]


    def cleanupUsers() {
        def users = User.list(params).findAll { !it.blogs?.size() }
        def count = userService.bulkDeleteUsers(users)
        flash.message = "Removed ${count} users"
        redirect(action: 'index', params: params)
    }

    def emailAboutNewGroovyblogs() {
        def usersWithActiveBlogs = User.list().findAll { it.blogs?.any {it.status == BlogStatus.ACTIVE}}

        userService.emailAboutNewGroovyBlogs(usersWithActiveBlogs)
        redirect(action: 'index')

    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond User.list(params), model:[userCount: User.count()]
    }

    def show(User user) {
        respond user
    }

    def create() {
        respond new User(params)
    }

    @Transactional
    def save(User user) {
        if (user == null) {
            notFound()
            return
        }

        if (user.hasErrors()) {
            respond user.errors, view:'create'
            return
        }

        user.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id] as Object[] )
                redirect user
            }
            '*' { respond user, [status: CREATED] }
        }
    }

    def edit(User user) {
        respond user
    }

    @Transactional
    def update(User user) {
        if (user == null) {
            notFound()
            return
        }

        if (user.hasErrors()) {
            respond user.errors, view:'edit'
            return
        }

        user.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), user.id])
                redirect user
            }
            '*'{ respond user, [status: OK] }
        }
    }

    @Transactional
    def delete(User user) {

        if (user == null) {
            notFound()
            return
        }

        user.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), user.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
