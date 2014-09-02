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
                "thumbnail.user" : "1234",
                "thumbnail.apiKey" : "yourkey",

                "translate.apikey" : "yourKey",

                "twitter.enabled" : "true",
                "twitter.user" : "youruser",
                "twitter.password" : "yourpassword"
            ]

            defaultConfig.each { key, value ->
                new SystemConfig(settingName: key, settingValue: value).save()
            }


        }

    }

    def createAdminUserIfRequired() {
        def config = grailsApplication.config.org.groovyblogs

        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true, failOnError: true)
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true, failOnError: true)


        def adminUser = new User(username: config.adminUser, password: config.adminPassword, email: "admin@groovyblogs.org")
        adminUser.save(flush: true, failOnError: true)

        UserRole.create adminUser, adminRole, true

        assert User.count() == 1
        assert Role.count() == 2
        assert UserRole.count() == 1
    }
} 