package org.groovyblogs

import grails.validation.Validateable

class RegisterAccountCommand implements Validateable {

    String username
    String password
    String email
    String recaptcha

    static constraints = {
        username nullable: false, validator: { value ->
            if(User.findByUsername(value)) {
                return ['duplicate']
            }
        }
        password nullable: false, minSize: 8, password: true
        email nullable: false, email: true
        recaptcha nullable: true
    }



}
