package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class ForgotPasswordController {

    private static final String PW_POOL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"

    def forgottenPassword() {

        if (!params.userid) {
            return
        }

        def account = User.findByUsername(params.userid)
        if (!account || !account.email) {
            flash.message = "Could not locate your account."
            redirect(controller: 'entries')
            return
        }

        def random = new Random()
        int length = PW_POOL.size() - 1
        StringBuilder genPw = new StringBuilder()
        8.times {
            genPw.append PW_POOL[random.nextInt(length)]
        }
        String password = genPw.toString()

        account.password = password.encodeAsSHA1Bytes().encodeBase64()
        String msg = """
            <h1>groovyblogs.org Password Reset</h1>
            <p>
            Hi ${account.username}, we've reset your password to: <b>${password}</b>.
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
    }
}
