package org.groovyblogs

class User {

    transient springSecurityService

    String username
    String name
    String twitter
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
        name nullable: true
        twitter nullable: true, unique: true
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

    @SuppressWarnings("UnnecessaryQualifiedReference")
    def beforeDelete() {
        UserRole.withNewSession {
            UserRole.findAllByUser(this)*.delete(flush: true)
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
