class Blog { 
	
    String feedUrl
    String title
    String description
    int pollFrequency = 3
    Date lastPolled = new Date()
    Date nextPoll = new Date()
    Date registered = new Date()
    String status = "PENDING"
    String lastError
	
    User account
    static belongsTo = User
	
    // def optionals = [ "title", "description", "pollFrequency", "status", "lastError", ]
	              	
    // Set blogEntries = new HashSet()
	
    static hasMany = [ blogEntries: BlogEntry ]
	                      
    static def constraints = {
        feedUrl(url:true,blank:false,unique:true,validator: { val,obj ->
                return !(val =~ /groovyblogs/)
            }
        )
        pollFrequency(inList:[1, 3, 12, 24] )
        title(nullable:true)
        description(nullable:true)
        status(nullable:true)
        lastError(nullable:true)
    }

    static mapping = {
        nextPolled index:'Next_Poll_Idx'
    }
	                      
}	
