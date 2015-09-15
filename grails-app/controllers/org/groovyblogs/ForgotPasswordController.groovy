package org.groovyblogs
import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class ForgotPasswordController {
    def userService

    static defaultAction = "forgottenPassword"

    def forgottenPassword(String identity) {
        if (!request.post) {
            return
        }
        if (identity) {
            def account = User.findByUsernameOrEmail(identity, identity)

            if (account) {
                if(account.email) {
                    userService.requestResetPassword(account)
                } else {
                    flash.message = "There is not email address associated with your account. Please contact info@groovyblogs.org for help"
                }

             }
            flash.message = "If we found your account you should now have email. Go ahead, check your inbox - or your spam-folder"
            redirect(controller: 'entries', action:"recent")
        } else {
            flash.message = "Please enter your account name or email address."
            redirect(action: 'forgottenPassword')
        }
    }

    def resetPassword(ResetPasswordCommand command) {
        if(command.hasTokenError()) {
            flash.message = "That's not right... The token was not found. Remember the token only lives 24 hours. Perhaps you could try again."
            redirect(controller: 'entries', action:"recent")
            return
        }
        if(request.post) {
            if(command.hasErrors()) {
                render(view: '/forgotPassword/resetPassword', model: [command: command])
            } else {
                if(userService.resetPassword(command)) {
                    flash.message = "Your password was reset."
                    redirect(controller: 'login')
                } else {
                    flash.message = "That did not work go right. You should try one more time."
                    redirect(controller: 'forgotPassword')
                }
            }
        } else {
            command.clearErrors()
            [command: command]
        }
    }
}

