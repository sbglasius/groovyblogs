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

    void resetPassword(User account) {
        def passwordToken = RandomStringUtils.randomAlphanumeric(64)
        passwordTokenCache.put(passwordToken, account)
        def link = grailsLinkGenerator.link(controller: 'forgotPassword', action: 'resetPassword', params:  [username: account.username, token: passwordToken], absolute: true)
        mailService.sendMail {
            to account.email
            subject "groovyblogs.org Password Reset"
            html groovyPageRenderer.render(template: '/mailtemplates/resetPassword', model: [username: account.username, link: link])
        }
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
