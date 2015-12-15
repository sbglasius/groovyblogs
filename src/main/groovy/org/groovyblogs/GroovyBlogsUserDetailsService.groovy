package org.groovyblogs

import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class GroovyBlogsUserDetailsService extends GormUserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        User.withTransaction { status ->
            def user = User.where {
                username == username || email == username
            }.get()
            if (!user) {
                log.warn "User not found: $username"
                throw new NoStackUsernameNotFoundException()
            }

            Collection<GrantedAuthority> authorities = loadAuthorities(user, username, loadRoles)
            createUserDetails user, authorities
        }
    }
}
