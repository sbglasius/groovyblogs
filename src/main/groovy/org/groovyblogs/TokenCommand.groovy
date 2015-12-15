package org.groovyblogs

import grails.validation.Validateable
import org.springframework.beans.factory.annotation.Autowired

class TokenCommand implements Validateable{
    @Autowired
    UserService userService

    String username
    String token

    static constraints = {
        username nullable: false, validator: { val, obj ->
            if(!obj.userService.pullUserFromTokenCache(obj.token, val)) {
                ['mismatch']
            }
        }
        token nullable: false
    }

    boolean hasTokenError() {
        this.errors?.fieldErrors?.field?.any {it in ['username','token']}
    }
}
