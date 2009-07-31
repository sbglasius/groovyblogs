import grails.util.Environment

class ApplicationBootStrap {
	
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
        def defaultProps = [
            'mail.host' : 'localhost',
            'mail.from' : 'root@localhost'
        ]
        defaultProps.each { key, value ->
            def setting = Setting.findByName(key)
            if (!setting) {
                println "Setting default property for: ${key}"
                new Setting(name: key, value: value).save()
            }

        }

    }

    def createAdminUserIfRequired() {
        
    }
} 