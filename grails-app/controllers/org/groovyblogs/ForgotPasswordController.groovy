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
                    userService.resetPassword(account)
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
        def fieldsWithErrors = command.errors.fieldErrors*.field
        if(fieldsWithErrors.any {it in ['username','token']}) {
            flash.message = "That's not right... did you tamper with the token? If not, please try and request a new password change."
            redirect(controller: 'entries', action:"recent")
            return
        }
        if(request.post) {
            if(command.hasErrors()) {
                render(view: '/forgotPassword/changePassword', model: [command: command])
            } else {
                render('ok')
            }
        } else {
            command.clearErrors()
            [command: command]
        }
    }
}

