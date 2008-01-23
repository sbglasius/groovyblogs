class EntriesController {

	FeedService feedService
	CacheService cacheService
	
	def index = { }
	
	// suppress when more than three entries from same author
	public static def limitEntries(entries) {
		
		def authorHash = [ : ] // count by blog
   		def limitEntries = [ ] // limit to three entries
   			
   		entries.each { entry ->
   			def key = entry.blog.feedUrl
   			int entryCount = authorHash[key] ? authorHash[key] : 0
   			entryCount++
   			authorHash[key] = entryCount
   			if (entryCount <= 3) {
   				if (entryCount == 3)
   					entry.info = "Reached limit of 3 displayed entries shown for ${entry.blog.title}. " +
   						"<a href='../blog/show/${entry.blog.id}'>Read more...</a>"
   				limitEntries << entry
   			} 
   		}
		return limitEntries
		
	}
	

	
	def recent = {
			
			def entries = cacheService.getFromCache("recentCache", 60, "recentList")
			if (!entries) {
				
				def aWhileAgo = new Date().minus(7) // 7 days ago

				entries = BlogEntry.findAllByDateAddedGreaterThan(
						aWhileAgo, [ sort: 'dateAdded', order: "desc" ] )
				entries = entries.findAll { entry -> entry.isGroovyRelated() }
				cacheService.putToCache("recentCache", 60, "recentList", entries)				
			}
			
			
			return [    entries: limitEntries(entries), 
			        	pageTitle : 'Recent Entries (Last 7 Days)',
			        	thumbnails: grailsApplication.config.thumbnail.enabled ]
	}
	
	def popular = {

			def entries = cacheService.getFromCache("popularCache", 60, "popularList")
			if (!entries) {
			
				def aWhileAgo = new Date().minus(7) // 7 days ago
				
				entries = BlogEntry.findAllByDateAddedGreaterThanAndHitCountGreaterThan(
					aWhileAgo, 0, [ sort: 'hitCount', order: "desc" ] )
				entries = entries.findAll { entry -> entry.isGroovyRelated() }
					
				cacheService.putToCache("popularCache", 60, "popularList", entries)	
			}
			
			render(view: 'recent', 
					model: [ entries: entries, 
					         pageTitle: 'Popular Entries (Last 7 Days)',
					         thumbnails: grailsApplication.config.thumbnail.enabled] 
			)
	}
	
	def lists = {
			def entries = feedService.getCachedListEntries()
			render(view: 'lists', 
					model: ['entries': entries] )
	}
	
	def jump = {
			
			BlogEntry be = BlogEntry.get(params.id)
			if (be) {
				//TODO should be transactional
				be.hitCount++
				be.save()
				response.sendRedirect(be.link)
			} else {
				flash.message = "Could not find link for blogEntry id $params.id"
				redirect(action: recent)
			}
			
	}
	
	def jumpTranslate = {
			
			BlogEntry be = BlogEntry.get(params.id)
			def lang = params.lang
			if (be && lang) {
				//TODO should be transactional
				be.hitCount++
				be.save()
				
				def engine = new groovy.text.SimpleTemplateEngine()
		    	def template = engine.createTemplate(grailsApplication.config.translate.url)
		    	def binding = [
	    	    	from: lang,
	    	        to: "en",
	    	        url: be.link,
    	        ]
    	        def jumpTranslateUrl = template.make(binding).toString()
				
				response.sendRedirect(jumpTranslateUrl)
			} else {
				flash.message = "Could not find link for blogEntry id $params.id"
				redirect(action: recent)
			}
			
	}
}

