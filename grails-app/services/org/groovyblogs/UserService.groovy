package org.groovyblogs

import grails.transaction.Transactional
import org.apache.commons.lang.RandomStringUtils
import org.springframework.cache.Cache

@Transactional
class UserService {
    def mailService
    def groovyPageRenderer
    def grailsLinkGenerator
    def grailsCacheManager

    int bulkDeleteUsers(List<User> users) {
        int count = users.size()
        users.each {
            it.delete(failOnError: true)
        }
        count
    }

    void requestResetPassword(User account) {
        log.info("Password change requested for $account.username")

        def passwordToken = RandomStringUtils.randomAlphanumeric(64)
        passwordTokenCache.put(passwordToken, account)
        def link = grailsLinkGenerator.link(controller: 'forgotPassword', action: 'resetPassword', params:  [username: account.username, token: passwordToken], absolute: true)
        mailService.sendMail {
            to account.email
            subject "groovyblogs.org Password Reset"
            html groovyPageRenderer.render(template: '/mailtemplates/resetPassword', model: [username: account.username, link: link])
        }
    }

    boolean resetPassword(ResetPasswordCommand command) {
        def user = pullUserFromToken(command.token, command.username)
        if(user) {
            user.password = command.newPassword
            user.passwordExpired = false
            user.save()
            // If no role for a given user, add it now...
            if(!UserRole.findByUser(user)) {
                def role = Role.findByAuthority('ROLE_USER')
                UserRole.create(user,role)
            }
            log.info("Password for $user.username was changed!")
            return true
        }
        return false
    }

    User pullUserFromToken(String token, String username) {
        def account = passwordTokenCache.get(token)?.get() as User
        if(account?.username == username) {
            return account.attach()
        }
    }

    private Cache getPasswordTokenCache() {
        def cache = grailsCacheManager.getCache('passwordTokens')
        return cache
    }
}
