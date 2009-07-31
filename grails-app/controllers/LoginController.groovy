class LoginController {
	
    def index = {
        render(view:'login')
    }
	
    def login = {
        if (params.userid && params.password) {
            def user = Account.findByUseridAndStatus(params.userid, "active")
	    		
            String calcPassword = params.password.encodeAsSHA1Bytes().encodeBase64()
            if (user != null && user.password == calcPassword)  {
                session.account = user
                user.lastLogin = new Date()
                user.save()
                flash.message = "Welcome ${user.userid}"
                if (session.returnController) {
                    redirect(controller:session.returnController, action:session.returnAction)
                } else {
                    redirect(controller:'entries', action:'recent')
                }
            } else {
                flash.message = "Invalid username or password. Please try again."
            }
        }
    }
	
    def forgottenPassword = {
	
        if (params.userid) {
			
            def account = Account.findByUserid(params.userid)
            if (account && account.email) {
    			
                def PW_POOL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
                def genPw = ""
                8.times {

                    genPw += PW_POOL[new Random().nextInt(PW_POOL.size() -1)]

                }
                account.password = genPw..encodeAsSHA1Bytes().encodeBase64()
                def msg = """
		        	<h1>groovyblogs.org Password Reset</h1>
		        	<p>
		            Hi ${account.userid}, we've reset your password to: <b>${genPw}</b>.
					You need to type in the letters in upper case. 
					Once you've logged on you can change it to something you prefer
					by going into the "My Blogs" tab.
		            </p>
					<p>
					Glen Smith - groovyblogs.org
					</p>
		            """
                sendMail {
                    to account.email
                    subject "groovyblogs.org Password Reset"
                    body msg
                }
                flash.message = "A new password has been generated and emailed to your account"
                redirect(controller: 'login')
            } else {
                flash.message = "Could not locate your account."
            }
			
        }
			
    }
	
    
    def logout = {
    		
        session.account = null
        flash.message = "You have successfully logged out"
        redirect(controller: 'entries', action:'recent')
    		
    }
}

