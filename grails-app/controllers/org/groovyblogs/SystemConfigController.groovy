package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

import org.springframework.dao.DataIntegrityViolationException

@Secured(['ROLE_ADMIN'])
class SystemConfigController {

    static defaultAction = 'list'

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [systemConfigInstanceList: SystemConfig.list(params), systemConfigInstanceTotal: SystemConfig.count()]
    }

    def create() {
        [systemConfigInstance: new SystemConfig(params)]
    }

    def save() {
        def systemConfigInstance = new SystemConfig(params)
        if (!systemConfigInstance.save(flush: true)) {
            render(view: "create", model: [systemConfigInstance: systemConfigInstance])
            return
        }

        flash.message = "${message(code: 'default.created.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), systemConfigInstance.id])}"
        redirect(action: "show", id: systemConfigInstance.id)
    }

    def show() {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (!systemConfigInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
            return
        }

        [systemConfigInstance: systemConfigInstance]
    }

    def edit() {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (!systemConfigInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
            return
        }

        [systemConfigInstance: systemConfigInstance]
    }

    def update() {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (!systemConfigInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
            return
        }

        if (params.version) {
            Long version = params.version as Long
            if (systemConfigInstance.version > version) {
                systemConfigInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'systemConfig.label', default: 'org.groovyblogs.SystemConfig')] as Object[], "Another user has updated this org.groovyblogs.SystemConfig while you were editing")
                render(view: "edit", model: [systemConfigInstance: systemConfigInstance])
                return
            }
        }

        systemConfigInstance.properties = params
        if (systemConfigInstance.hasErrors() || !systemConfigInstance.save(flush: true)) {
            render(view: "edit", model: [systemConfigInstance: systemConfigInstance])
            return
        }

        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), systemConfigInstance.id])}"
        redirect(action: "show", id: systemConfigInstance.id)
    }

    def delete() {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (!systemConfigInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
            return
        }

        try {
            systemConfigInstance.delete(flush: true)
            flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }
}
