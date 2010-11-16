import grails.util.Environment

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
        

    }

    def createAdminUserIfRequired() {
        if (Account.count() == 0) {
        	
        	def admin = new Account(userid: "admin", role: "admin",
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