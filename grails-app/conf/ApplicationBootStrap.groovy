import org.apache.commons.logging.LogFactory

class ApplicationBootStrap {

	 //def log = LogFactory.getLog(ApplicationBootStrap.class.name)
	
	 SearchService searchService
	
     def init = { servletContext ->
     
     	log.info("Optimising index starting at " + new Date())
     	searchService = new SearchService()
     	searchService.deleteIndex()
     	def entries = BlogEntry.list()
     	log.info("Attempting to index "  + entries.size() + " entries")
     	searchService.indexAll(entries)
     	log.info("Index complete, optimising")
     	searchService.optimise()
     	log.info("Optimising index complete at " + new Date())
        
     }
	 
     def destroy = {
			 
		net.sf.ehcache.CacheManager.getInstance().shutdown()
		
     }
} 