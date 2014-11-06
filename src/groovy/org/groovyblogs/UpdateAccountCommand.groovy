package org.groovyblogs

import grails.validation.Validateable

@Validateable
class UpdateAccountCommand {
    String name
    String twitter
    String newPassword
    String repeatPassword
    String email

    static constraints = {
        name nullable: true
        twitter nullable: true
        newPassword nullable: true, minSize: 8, password: true
        repeatPassword nullable: true, password: true, validator: { val, obj ->
            if(val != obj.newPassword) {
                ['passwords-does-not-match']
            }
        }
        email nullable: false, email: true
    }



}
