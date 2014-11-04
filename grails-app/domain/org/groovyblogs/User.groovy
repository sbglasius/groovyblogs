package org.groovyblogs

class User {

    transient springSecurityService

    String username
    String password
    String email
    String unconfirmedEmail
    boolean enabled = true
    Date lastLogin = new Date()
    Date registered = new Date()
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    static hasMany = [blogs: Blog]

    static constraints = {
        username unique: true
        email email: true
        unconfirmedEmail nullable: true, email: true
    }

    static mapping = {
        table name: 'account'
        username index: 'User_Id_Idx'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    def beforeDelete() {
        UserRole.withNewSession {
            UserRole.findAllByUser(this)*.delete()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
