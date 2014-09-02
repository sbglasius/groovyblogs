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
        	
        	def admin = new User(username: "admin", role: "admin",
        	      status: "ACTIVE", email: "glen@bytecode.com.au")
        
        	def password =  "admin".encodeAsSHA1Bytes().encodeBase64()
        	println "Admin password is encoded to ${password}"
        	admin.password = password
        	      
            if (!admin.save()) {
            	println "Failed to create admin user: ${admin.errors}" 
            }
        }
    }
} 