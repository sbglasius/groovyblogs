package org.groovyblogs

class UserService {

    int bulkDeleteUsers(List<User> users) {
        int count = users.size()
        users.each {
            it.delete(failOnError: true)
        }
        count
    }
}
