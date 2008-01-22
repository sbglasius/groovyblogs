class Blog { 
	
	String feedUrl
	String title
	String description
	int pollFrequency = 3
	Date lastPolled = new Date()
	Date nextPoll = new Date()
	Date registered = new Date()
	String status
	String lastError
	
	Account account	
	static belongsTo = Account
	
	// def optionals = [ "title", "description", "pollFrequency", "status", "lastError", ]
	              	
	// Set blogEntries = new HashSet()
	
	static hasMany = [ blogEntries: BlogEntry ]
	                      
	static def constraints = {
	     feedUrl(url:true,blank:false,unique:true,validator: { val,obj ->
	     		return !(val =~ /groovyblogs.org/)
	     	}
	     )
	     pollFrequency(inList:[1, 3, 12, 24] )
	     title(nullable:true)
	     description(nullable:true)
	     status(nullable:true)
	     lastError(nullable:true)
	}
	                      
}	
