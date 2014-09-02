package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class ForgotPasswordController {

    def forgottenPassword() {

        if (params.userid) {
            def account = User.findByUsername(params.userid)
            if (account && account.email) {

                def PW_POOL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
                def genPw = ""
                8.times {

                    genPw += PW_POOL[new Random().nextInt(PW_POOL.size() - 1)]

                }
                account.password = genPw.encodeAsSHA1Bytes().encodeBase64()
                def msg = """
		        	<h1>groovyblogs.org Password Reset</h1>
		        	<p>
		            Hi ${account.username}, we've reset your password to: <b>${genPw}</b>.
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
                redirect(controller: 'entries')
            } else {
                flash.message = "Could not locate your account."
            }

        }

    }
}

