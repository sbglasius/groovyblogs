import net.sf.ehcache.Ehcache

class ThumbnailController {
	
    ThumbnailService thumbnailService

    def index = {
        redirect(action: show)
    }

	
    private void writeImage(def response, String imgSize) {
		
		byte[] b = thumbnailService.getFile(params.id, imgSize)
        response.setContentType("image/jpeg")
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
		
    }
	
    def show = {

        writeImage(response, "small")
		
    }
	
    def showLarge = {

        writeImage(response, "large")
		
    }

	
	
}

