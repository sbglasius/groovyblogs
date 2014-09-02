class User {

    transient springSecurityService

    String username
    String password
    String email
    String status
    String role = "user"
    Date lastLogin = new Date()
    Date registered = new Date()

    static hasMany = [blogs: Blog]
    static transients = ['springSecurityService']

    static def constraints = {
        username nullable: false, unique: true
        password nullable: false
        email email: true, nullable: false
        status nullable: true
        role nullable: true
    }

    static mapping = {
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

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}	
