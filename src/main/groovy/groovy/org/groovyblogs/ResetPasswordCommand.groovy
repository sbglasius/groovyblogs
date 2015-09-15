package org.groovyblogs
import grails.validation.Validateable

@Validateable
class ResetPasswordCommand extends TokenCommand {
    String newPassword
    String repeatPassword

    static constraints = {
        newPassword nullable: false, minSize: 8, password: true
        repeatPassword nullable: true, password: true, validator: { val, obj ->
            if(val != obj.newPassword) {
                ['passwords-does-not-match']
            }
        }
    }
}
