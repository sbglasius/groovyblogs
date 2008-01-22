class Account {
	
	String userid
	String password
	String email
	String status
	String role = "user"
	Date lastLogin = new Date()
	Date registered = new Date()
	
	// def optionals = [ "status", "role"]
	
	// Set blogs = new HashSet()
	
	static hasMany = [ blogs: Blog ]

	static def constraints = {
	     userid(size:3..25,blank:false,unique:true)
	     password(size:1..50,blank:false)
	     email(email:true,blank:false)
	     status(nullable:true)
	     role(nullable:true)
	}
	
}	
