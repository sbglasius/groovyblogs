package org.groovyblogs

import grails.events.annotation.gorm.Listener
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.springframework.beans.factory.InitializingBean

@CompileStatic
@Slf4j
class UserPasswordEncoderService implements InitializingBean {

    SpringSecurityService springSecurityService

    @Listener(User)
    void onPreInsertEvent(PreInsertEvent event) {
        encodePasswordForEvent(event)
    }

    @Listener(User)
    void onPreUpdateEvent(PreUpdateEvent event) {
        encodePasswordForEvent(event)
    }

    private void encodePasswordForEvent(AbstractPersistenceEvent event) {
        User user = event.entityObject as User
        if (user.password && ((event instanceof PreInsertEvent) || (event instanceof PreUpdateEvent && user.isDirty('password')))) {
            log.debug("Encoding password for $user")
            event.entityAccess.setProperty('password', encodePassword(user.password))
        }
    }

    private String encodePassword(String password) {
        springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    @Override
    void afterPropertiesSet() throws Exception {
        log.debug("Initialized UserPasswordEncoderService")

    }
}
