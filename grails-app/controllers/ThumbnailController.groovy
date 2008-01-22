import groovy.net.xmlrpc.*

class ThumbnailController {
	
	CacheService cacheService
	ThumbnailService thumbnailService

	def index = {
			redirect(action: show)
	}


	
	private void writeImage(def response, byte[] b) {
		
		response.setContentType("image/jpeg")		 
		response.setContentLength(b.length)
		response.getOutputStream().write(b)
		
	}
	
	def show = { 
			
			def id = params.id
			
	    	// grab thumb bytes from cache if possible
			byte[] b = cacheService.getFromCache("thumbCache", 3600, "small-" + id)
			if (!b) {			
				b = thumbnailService.getFile(id, true, false)
				cacheService.putToCache("thumbCache", 3600, "small-" + id, b)				
			}

			writeImage(response, b)
		
	}
	
	def showLarge = {
		
		def id = params.id
		
    	// grab thumb bytes from cache if possible
		byte[] b = cacheService.getFromCache("thumbCache", 3600, "big-" + id)
		if (!b) {			
			b = thumbnailService.getFile(id, false, false)
			cacheService.putToCache("thumbCache", 3600, "big-" + id, b)				
		}
		
		writeImage(response, b)
		
	}
	
	def preview = {
	
		def url = params.url
		
		def tempThumbsDir = grailsApplication.config.thumbnail.tmpdir
		
		def thumbnail = "${tempThumbsDir}/" + url.encodeAsTempFile() + ".png"
		def thumbnailBig = "${tempThumbsDir}/" + url.encodeAsTempFile() + "-orig.png"
		
		def f = new File(thumbnail)
		
		if (!f.exists()) {
			thumbnailService.writeFile(url, thumbnailBig, thumbnail)
		}
		
		byte[] b = f.readBytes()
		
		writeImage(response, b)
		
	}
	
	
}

