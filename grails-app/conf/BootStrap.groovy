import grails.util.Environment
import org.groovyblogs.Role
import org.groovyblogs.SystemConfig
import org.groovyblogs.User
import org.groovyblogs.UserRole

class BootStrap {
    def grailsApplication

    def init = { servletContext ->

        switch (Environment.current) {

            case Environment.DEVELOPMENT:
            case Environment.PRODUCTION:
                createConfigurationIfRequired()
                createAdminUserIfRequired()
                break;

        }


    }

    def destroy = {


    }

    def createConfigurationIfRequired() {

        if (SystemConfig.count() == 0) {

            def defaultConfig = [
                    "thumbnail.user"  : "1234",
                    "thumbnail.apiKey": "yourkey",

                    "translate.apikey": "yourKey",

                    "twitter.enabled" : "true",
                    "twitter.user"    : "youruser",
                    "twitter.password": "yourpassword"
            ]

            defaultConfig.each { key, value ->
                new SystemConfig(settingName: key, settingValue: value).save()
            }


        }

    }

    def createAdminUserIfRequired() {
        def config = grailsApplication.config.org.groovyblogs
        def adminRole = Role.findOrSaveWhere(authority: 'ROLE_ADMIN').save(flush: true, failOnError: true)
        def userRole = Role.findOrSaveWhere(authority: 'ROLE_USER').save(flush: true, failOnError: true)

        def adminUser = User.findByUsername(config.adminUser)
        if (adminUser) {
            adminUser.password = config.adminPassword
            adminUser.save(flush: true, failOnError: true)
        } else {
            adminUser = new User(username: config.adminUser, password: config.adminPassword, email: "admin@groovyblogs.org")
            adminUser.save(flush: true, failOnError: true)

        }

        if (!UserRole.exists(adminUser.id, adminRole.id)) {
            UserRole.create(adminUser, adminRole, true)
        }
    }
} 