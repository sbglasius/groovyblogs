package org.groovyblogs

import grails.transaction.Transactional

@Transactional
class UserService {

    int bulkDeleteUsers(List<User> users) {
        def count = users.size()
        users.each {
            it.delete(failOnError: true)
        }
        count
    }
}
