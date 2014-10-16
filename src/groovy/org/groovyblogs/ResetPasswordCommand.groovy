package org.groovyblogs

import grails.validation.Validateable
import org.springframework.beans.factory.annotation.Autowired

@Validateable
class ResetPasswordCommand {
    @Autowired
    UserService userService

    String username
    String token
    String newPassword
    String repeatPassword

    static constraints = {
        username nullable: false, validator: { val, obj ->
            if(!obj.userService.pullUserFromToken(obj.token, val)) {
                ['mismatch']
            }
        }
        token nullable: false
        newPassword nullable: false, minSize: 8, password: true
        repeatPassword nullable: false, password: true, validator: { val, obj ->
            if(val != obj.repeatPassword) {
                ['passwords-does-not-match']
            }
        }
    }
}
