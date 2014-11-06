package org.groovyblogs

import grails.plugin.springsecurity.userdetails.GrailsUser
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent

class AuthenticationSuccessEventListener implements
        ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    @Override
    void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        GrailsUser grailsUser = event.authentication.principal as GrailsUser

        User.withNewSession {
            def user = User.get(grailsUser.id)
            println user
            user.lastLogin = new Date()
            user.save(flush: true)
        }
    }
}
