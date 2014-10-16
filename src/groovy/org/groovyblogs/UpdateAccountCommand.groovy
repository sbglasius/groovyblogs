package org.groovyblogs

import grails.validation.Validateable

@Validateable
class UpdateAccountCommand {

    String newPassword
    String repeatPassword
    String email

    static constraints = {
        newPassword nullable: true, minSize: 8, password: true
        repeatPassword nullable: true, password: true, validator: { val, obj ->
            if(val != obj.newPassword) {
                ['passwords-does-not-match']
            }
        }
        email nullable: false, email: true
    }



}
