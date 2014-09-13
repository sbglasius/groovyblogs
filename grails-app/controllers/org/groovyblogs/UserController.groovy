package org.groovyblogs

@Secured(['ROLE_ADMIN'])
class UserController {
    static scaffold = true
}
