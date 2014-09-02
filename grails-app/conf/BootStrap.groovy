import grails.util.Environment
import org.groovyblogs.SystemConfig
import org.groovyblogs.User

class BootStrap {
	
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
        if (User.count() == 0) {
        	
        	def admin = new User(username: "admin", password: "admin", role: "admin",
        	      status: "ACTIVE", email: "glen@bytecode.com.au")
        
            if (!admin.save()) {
            	println "Failed to create admin user: ${admin.errors}" 
            }
        }
    }
} 