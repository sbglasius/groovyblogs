class BlogEntry { 
	
	String title
	String description
	String link
	Date dateAuthored = new Date()
	Date dateAdded = new Date()
	int hitCount = 0
	String language
	String info
	
	// info is used for sticking stuff like "Only showing 3 entries for this user"
	static def transients = [ "info" ]
	// static def optionals = [ "language" ]
	
	Blog blog	
	
	static belongsTo = Blog
	
	static def constraints = {
	     title(size:0..255)
	     description(size:0..4096)
	     language(nullable:true)
         link(unique: true)
    }
	
	
	def indexedFields() {
		
		def fields = [:]
		// strip html before storing in index
		fields.title = title.replaceAll("\\<.*?\\>","");
		fields.description = description.replaceAll("\\<.*?\\>","");
		fields.dateAdded = "" + dateAdded.getTime();
		fields.blogTitle = blog.title;

		return fields
		
	}
	
	def isGroovyRelated() {
	
		def keywords = [ 'groovy', 'grails' ]
		                 
        boolean isGroovy = false
 	    keywords.each { keyword -> 
 	    	if (title?.toLowerCase() =~ keyword)
 	    		isGroovy = true
 	      			
 	      	if (description?.toLowerCase() =~ keyword)
 	      		isGroovy = true
 	      		
 	    }
 	    return isGroovy
	}

	String toThumbnailPath() {

        def sdf = new java.text.SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(dateAdded) + "/" 
        
    }
}	
