package org.groovyblogs

import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

class BlogEntryController {

    BlogEntryService blogEntryService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    EntriesService entriesService

    def toggleDisableFlag(BlogEntry blogEntry) {
        log.debug("toggleDisableFlag: $blogEntry")
        if (blogEntry) {
            entriesService.toggleDisableFlag(blogEntry)
        }
        redirect(controller: 'entries')
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond blogEntryService.list(params), model: [blogEntryCount: blogEntryService.count()]
    }

    def show(Long id) {
        respond blogEntryService.get(id)
    }

    def create() {
        respond new BlogEntry(params)
    }

    def save(BlogEntry blogEntry) {
        if (blogEntry == null) {
            notFound()
            return
        }

        try {
            blogEntryService.save(blogEntry)
        } catch (ValidationException e) {
            respond blogEntry.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'blogEntry.label', default: 'BlogEntry'), blogEntry.id])
                redirect blogEntry
            }
            '*' { respond blogEntry, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond blogEntryService.get(id)
    }

    def update(BlogEntry blogEntry) {
        if (blogEntry == null) {
            notFound()
            return
        }

        try {
            blogEntryService.save(blogEntry)
        } catch (ValidationException e) {
            respond blogEntry.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'blogEntry.label', default: 'BlogEntry'), blogEntry.id])
                redirect blogEntry
            }
            '*' { respond blogEntry, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        blogEntryService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'blogEntry.label', default: 'BlogEntry'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'blogEntry.label', default: 'BlogEntry'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
