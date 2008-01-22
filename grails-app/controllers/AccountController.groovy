
            
class AccountController extends BaseController {
	
	MailService mailService
	CryptoService cryptoService
	FeedService feedService
	SearchService searchService

	
    def index = { redirect(action:'edit',params:params) }
    

    def edit = {
        def account = Account.get( session.account.id )

        if(!account) {
                flash.message = "Account not found with id ${session.account.id}"
                redirect(action:'list')
        }
        else {
            return [ account : account ]
        }
    }

    def update = {
        def account = Account.get( params.id )
        if(account.id == session.account.id) {
             account.properties = params
             account.password = cryptoService.sha1(params.password.getBytes())
            if(account.save()) {
            	flash.message = "Updated successfully"
                redirect(action:'edit', model:[account:account])
            }
            else {
                render(view:'edit',model:[account:account])
            }
        }
        else {
            flash.message = "Account not found with id ${params.id}"
            redirect(action:'edit',id:params.id)
        }
    }

    def signup = {
        def account = new Account()
        account.properties = params
        return ['account':account]
    }
	
    def register = {
        def account = new Account()
        account.properties = params
        account.registered = new Date()
        account.status = "active"
        account.password = cryptoService.sha1(params.password.getBytes())
        
        if(account.save()) {
        	session.account = account
        	redirect(action:'edit')
        	//render(view: 'addfeed', model: ['account':account ])
			return
        } else {
        	account.password = params.password
            render(view:'signup',model:[account:account])
        }
    }
	
	
	def deleteFeed = {
	        def blog = Blog.get( params.id )
	        if(blog) {
	        	if (blog.account.id == session.account.id) {
	        		searchService.unindexAll(blog.blogEntries)
		            blog.delete()
		            
		            flash.message = "Successfully deleted blog ${blog.title}"
		    	} else {
		    		flash.message = "You don't have rights to delete that blog"
		    	}	

	        } else {
	        	flash.message = "Blog not found with id ${params.id}"
	        }
	        redirect(action: 'edit')
	    }
	
	def addFeed = {
			
		def feedUrl = params.feedUrl
		log.info("Adding Feed: [$feedUrl]")
    	if (feedUrl) {
    		def blog = new Blog()
            blog.properties = params
            def feedInfo = feedService.getFeedInfo(params.feedUrl)
            
            blog.title = feedInfo.title ? feedInfo.title : ""
            blog.title = blog.title.length() > 250 ? blog.title[0..249] : blog.title 		
            blog.description = feedInfo.description ? feedInfo.description : ""
            blog.description = blog.description.length() > 250 ? blog.description[0..249] : blog.description
            
            blog.account = Account.get(session.account.id)
            if(blog.save()) {
            	feedService.updateFeed(blog)
            	flash.message = "Successfully added new feed: ${feedInfo.title}"
            } else {
            	flash.message = "Error adding feed: ${blog.errors.allErrors[0].defaultMessage}"
            }

    	} else {
    		flash.message = "Could not determine feed url"
    	}
		redirect(action: 'edit')
	}
	
	def updateFeed = {
			def blog = Blog.get(params.id)
			log.info("Updating Feed: [${blog?.feedUrl}]")
	    	if (blog) {
	            def feedInfo = feedService.updateFeed(blog)
	            flash.message = "Successfully updated ${blog.title}"
	    	} else {
	    		flash.message = "Could not determine blog id"
	    	}	
			redirect(action: 'edit')
			
	}
    
    def testFeed = {
    
    	def feedUrl = params.feedUrl
    	log.debug("Testing Feed: [$feedUrl]")
    	if (feedUrl) {
    		def feedInfo = feedService.getFeedInfo(feedUrl)
    		log.debug("Returned $feedInfo.title $feedInfo.description $feedInfo.type")
    		def writer = new StringWriter()
    		def html = new groovy.xml.MarkupBuilder(writer)
    		
    		// Could do all this directly in a render() call but it's harder to debug
    		html.div {
					div(id: "iconDeets") { 
   						p(style: 'margin-top: 3px; margin-bottom: 3px') {
   								
								img(src: "../images/accept.png", 
									alt: "This is a groovy related post")
								span("Groovy/Grails Post ")	
								img(src: "../images/cancel.png", 
										alt: "Not a groovy related post",
										style: "margin-left: 5px;")
								span("Non Groovy/Grails Post (won't be aggregated) ")											
   						}
   					}

       				div(id: "blogInfo") {
       					div(id: "blogTitle") { p(feedInfo?.title) }
       					div(id: "blogDesc")  { p(feedInfo?.description) }
       					div(id: "blogType")  { p(feedInfo?.type) }
       					div(id: "blogEntries") {
       						for (e in feedInfo?.entries) {
       							div(class: "blogEntry") {
       								div(class: "blogEntryTitle") {
       									
       									p {
       										def isGroovyRelated = new BlogEntry(title: e.title, description: e.description).isGroovyRelated() 
       										img(src: 
       												isGroovyRelated ? "../images/accept.png" : "../images/cancel.png", 
       											alt: 
       												isGroovyRelated ? "This is a groovy related post" : "Not a groovy related post",
       										)
       											
       										span(e?.title)  
       									}
       								}
       								div(class: "blogEntryDesc") { p(e?.summary) }
       							}
       						}
       					
       					}
       				}
    			
    		}
    		
    		log.debug(writer.toString())
    		
    		render(writer.toString())
    		
    
    	} else {
    		render "You need to provide a URL for me"	
    	}
    	
    }
 
	

}