package org.groovyblogs

import grails.transaction.Transactional
import org.apache.commons.lang.RandomStringUtils
import org.springframework.cache.Cache

@Transactional
class UserService {
    public static final String TOKENS_CACHE = 'tokens'

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

    void requestResetPassword(User user) {
        log.info("Password change requested for $user.username")

        def passwordToken = RandomStringUtils.randomAlphanumeric(64)
        cache.put(passwordToken, user)
        def link = grailsLinkGenerator.link(controller: 'forgotPassword', action: 'resetPassword', params:  [username: user.username, token: passwordToken], absolute: true)
        mailService.sendMail {
            to user.email
            subject "groovyblogs.org Password Reset"
            html groovyPageRenderer.render(template: '/mailtemplates/resetPassword', model: [username: user.username, link: link])
        }
    }

    boolean resetPassword(ResetPasswordCommand command) {
        def user = pullUserFromTokenCache(command.token, command.username, true)
        if(user) {
            user.password = command.newPassword
            user.passwordExpired = false
            user.save(failOnError: true)
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

    /**
     * Get the user object from the token cache if token and username matches
     * @param token
     * @param username
     * @return the user that matches the token or null
     */
    User pullUserFromTokenCache(String token, String username, boolean remove = false) {
        def user = cache.get(token, User)
        if(user?.username == username) {
            if(remove) {
                cache.evict(token)
            }
            return user.attach()
        }
    }

    UpdateUserStatus updateUser(User user, UpdateAccountCommand command) {
        def status = UpdateUserStatus.NONE

        if(user.email != command.email) {
            user.unconfirmedEmail = command.email
            user.save(failOnError: true, flush: true)
            sendConfirmEmail(user)
            status = UpdateUserStatus.NEW_EMAIL
        }
        if(command.newPassword) {
            user.password = command.newPassword
            user.save(failOnError: true)
            status = status == UpdateUserStatus.NONE ? UpdateUserStatus.PASSWORD_UPDATED : UpdateUserStatus.BOTH
        }
        return status
    }



    boolean confirmEmail(TokenCommand command) {
        def user = pullUserFromTokenCache(command.token, command.username, true)
        if(user) {
            user.email = user.unconfirmedEmail
            user.unconfirmedEmail = ''
            user.save(failOnError: true)
            log.info("Confirmed email for user.username: $user.email")

            return true
        }
        return false
    }

    void sendConfirmEmail(User user) {
        log.info("Requesting confirm email for $user.username")
        def emailToken = RandomStringUtils.randomAlphanumeric(64)
        cache.put(emailToken, user)
        def link = grailsLinkGenerator.link(controller: 'account', action: 'confirmEmail', params: [username: user.username, token: emailToken], absolute: true)
        mailService.sendMail {
            to user.unconfirmedEmail
            subject "groovyblogs.org Confirm Email"
            html groovyPageRenderer.render(template: '/mailtemplates/confirmEmail', model: [username: user.username, link: link])
        }
    }

    private Cache getCache() {
        def cache = grailsCacheManager.getCache(TOKENS_CACHE)
        return cache
    }
}
