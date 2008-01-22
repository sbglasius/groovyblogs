import org.apache.commons.logging.LogFactory

/**

	A simple taglib to cache page fragments.
	
	@author Glen Smith
	
*/

class CacheTagLib {

	CacheService cacheService
	
	//def log = LogFactory.getLog(CacheTagLib.class.name)
	
	def cache = { attrs, body ->
    
		def cacheName = "cacheTaglib"
	    def cacheKey = attrs.key
	    def cacheTimeout = attrs.timeout ? Integer.parseInt(attrs.timeout) : 60
	    def cacheValue = cacheService.getFromCache (cacheName, cacheTimeout, cacheKey)
	    if (cacheValue) {
	        out << cacheValue
	        log.debug("Retrieved cached $cacheKey with value $cacheValue")
	    } else {
	    	def bodyVal = TagLibUtil.outToString(body,attrs)
	        out << bodyVal
	        log.debug("Storing to cache $cacheKey with value $bodyVal")
	        cacheService.putToCache(cacheName, cacheTimeout, cacheKey, bodyVal)
	    }
	}
}