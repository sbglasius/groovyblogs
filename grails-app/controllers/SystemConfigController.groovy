class SystemConfigController {

    def index () {
        redirect(action: "list", params: params)
    }

    def list () {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [systemConfigInstanceList: SystemConfig.list(params), systemConfigInstanceTotal: SystemConfig.count()]
    }

    def create () {
        def systemConfigInstance = new SystemConfig()
        systemConfigInstance.properties = params
        return [systemConfigInstance: systemConfigInstance]
    }

    def save () {
        def systemConfigInstance = new SystemConfig(params)
        if (systemConfigInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), systemConfigInstance.id])}"
            redirect(action: "show", id: systemConfigInstance.id)
        }
        else {
            render(view: "create", model: [systemConfigInstance: systemConfigInstance])
        }
    }

    def show () {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (!systemConfigInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
        }
        else {
            [systemConfigInstance: systemConfigInstance]
        }
    }

    def edit () {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (!systemConfigInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [systemConfigInstance: systemConfigInstance]
        }
    }

    def update () {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (systemConfigInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (systemConfigInstance.version > version) {
                    
                    systemConfigInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'systemConfig.label', default: 'SystemConfig')] as Object[], "Another user has updated this SystemConfig while you were editing")
                    render(view: "edit", model: [systemConfigInstance: systemConfigInstance])
                    return
                }
            }
            systemConfigInstance.properties = params
            if (!systemConfigInstance.hasErrors() && systemConfigInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), systemConfigInstance.id])}"
                redirect(action: "show", id: systemConfigInstance.id)
            }
            else {
                render(view: "edit", model: [systemConfigInstance: systemConfigInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete () {
        def systemConfigInstance = SystemConfig.get(params.id)
        if (systemConfigInstance) {
            try {
                systemConfigInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'systemConfig.label', default: 'SystemConfig'), params.id])}"
            redirect(action: "list")
        }
    }
}
