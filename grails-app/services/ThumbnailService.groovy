import org.codehaus.groovy.grails.commons.ConfigurationHolder
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.HttpClient

import java.awt.image.BufferedImage
import javax.imageio.*

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.io.ByteArrayInputStream


/**
 * @author Glen Smith
 */
public class ThumbnailService {

    Ehcache thumbCache
    Ehcache pendingCache
    byte[] noThumbAvailablePic

    public void fetchThumbnailsToCache(long id, String url) {

        byte[] small = fetchThumbnail(url, "medium")
        def smallCacheEntry = "${id}-small".toString()
        thumbCache.put(new Element(smallCacheEntry, small))

        byte[] large = fetchThumbnail(url, "medium2")
        def largeCacheEntry = "${id}-large".toString()
        thumbCache.put(new Element(largeCacheEntry, large)) 
        
        byte[] iphone = fetchThumbnail(url, "small")
        def iphoneCacheEntry = "${id}-iphone".toString()
        thumbCache.put(new Element(iphoneCacheEntry, iphone))

        

    }

    public byte[] fetchThumbnail(String url, String imgSize="large") {

        log.debug "Fetching remote thumbnail for ${url} of size ${imgSize}"
        def sdf = new java.text.SimpleDateFormat("yyyyMMdd")
        TimeZone tz = TimeZone.getTimeZone("GMT")
        sdf.setTimeZone(tz)
        def date = sdf.format(new Date())

        def user = ConfigurationHolder.config.thumbnail.user
        def apiKey = ConfigurationHolder.config.thumbnail.apiKey
        log.debug("Setting date to ${date}")
        
        def stringToHash = "${date}${url}${apiKey}"
        def hash = stringToHash.encodeAsMD5()
        log.debug("Hash is ${hash} of ${date}${url}${apiKey}")

        GetMethod get = new GetMethod(ConfigurationHolder.config.thumbnail.endpointurl)
        def nvp = [
            new NameValuePair("user", user.toString()),
            new NameValuePair("url", url.toString()),
            new NameValuePair("size", imgSize.toString()),
            new NameValuePair("cache", "7"),
            new NameValuePair("hash", hash.toString())
        ]
        get.setQueryString( nvp as NameValuePair[] )

        HttpClient httpclient = new HttpClient()

		if (ConfigurationHolder.config.http.useproxy) {
	        def hostConfig = httpclient.getHostConfiguration()
	        hostConfig.setProxy(ConfigurationHolder.config.http.host, ConfigurationHolder.config.http.port as int)
	        log.warn("Setting proxy to [" + ConfigurationHolder.config.http.host + "]")
	    }

        httpclient.executeMethod(get)
        byte[] image = get.getResponseBody(1024*100)
        log.debug "Fetch of ${url} complete"
        return image

    }

    /** Requires the ImageTools plugin
    byte[] scale2(byte[] srcFile, int destWidth, int destHeight) throws IOException {
        def imageTool = new ImageTool()
        imageTool.load(srcFile)
        imageTool.thumbnail(destWidth)
        return imageTool.getBytes("JPEG")
    }
    */

    // image scaling stuff from http://www.velocityreviews.com/forums/t148931-how-to-resize-a-jpg-image-file.html
    byte[] scale(byte[] srcFile, int destWidth, int destHeight) throws IOException {
        BufferedImage src = ImageIO.read(new ByteArrayInputStream(srcFile));
        BufferedImage dest = new BufferedImage(destWidth, destHeight,
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
            (double) destWidth / src.getWidth(),
            (double) destHeight / src.getHeight());
        g.drawRenderedImage(src, at);
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(dest, "JPG", baos);
        return baos.toByteArray()
    }

 
    public byte[] getFile(String id, String thumbSize) {

        if (!ConfigurationHolder.config.thumbnail.enabled) {
            log.debug("Thumbnail service disabled")
            return new byte[0]
        }

        log.info "Fetching thumbnail for id $id"

        def cacheEntry = "${id}-${thumbSize}".toString()
        byte[] t = thumbCache.get(cacheEntry)?.value
        if (t && t.length > 0) {
            log.debug "Found file in the cache..."
            return t
        }

        BlogEntry entry = BlogEntry.get(id)
        //log.debug entry.dump()
        def image
        try {
            //if (!entry.thumbnail || entry.thumbnail.size() < 100) { // Image is too small, corrupted...
                // entry.thumbnail = fetchThumbnail(entry.link)
                pendingCache.put(new Element(entry.link, entry.id))
                if (!noThumbAvailablePic) {
                    def noThumbUrl = this.class.getResource("/resources/no-thumb.jpg")
                    log.debug "The noThumb image is ${noThumbUrl}"
                    noThumbAvailablePic = new File(noThumbUrl.toURI()).readBytes()

                    //def noThumbAvailablePic = new File(noThumbUrl.toURI()).readBytes()
                    //entry.thumbnail = new File(noThumbUrl.toURI()).readBytes()
                }
                image = noThumbAvailablePic
                // entry.thumbnail = noThumbAvailablePic
            //}
            //log.debug "Thumb is: ${entry.thumbnail}"
            // all images stored as 640x480, resize them here
            switch(thumbSize) {
                case "small":
                    image = scale(image, 170, 124)
                    break;
                case "large":
                    image = scale(image, 512, 373)
                    break;
                case "iphone":
                    image = scale(image, 80, 60)
                    break;

            }
            
        } catch(Exception e) {
            log.warn "Thumb retrieval failed for ${id}", e
            image = new byte[0]
        }
        // thumbCache.put(new Element(cacheEntry, image))
        return image
 
    }



    /*
        Old approach

        public void writeFile(String url, String thumbpathBig, String thumbpath) {

        log.warn("Staring download on $url ...")

        def server = new XMLRPCServerProxy(ConfigurationHolder.config.thumbnail.serviceurl)

        Thread.start {
            def list = server.getThumbnail(url)

            if (list[0].length) {
                FileOutputStream big = new FileOutputStream(thumbpathBig)
                big << list[0]
            }

            if (list[1].length) {
                FileOutputStream small = new FileOutputStream(thumbpath)
                small << list[1]
            }
        }

    }

    public byte[] getFile(String id, boolean smallSize, boolean writeThumb) {

        if (!ConfigurationHolder.config.thumbnail.enabled) {
            log.debug("Thumbnail service disabled")
            return new byte[0]
        }

        log.info "Fetching thumbnail for id $id"


        byte[] t = cacheService.getFromCache("thumbFileCache", 3600, "${id}-${smallSize}")
        if (t && t.length > 0) {
            log.debug "Found file in the cache..."
            return t
        }

        BlogEntry entry = BlogEntry.get(id)

        def thumbsDir = ConfigurationHolder.config.thumbnail.dir
        def thumbsPath = "${thumbsDir}/${entry.toThumbnailPath()}"

        if (entry && writeThumb && !(new File(thumbsPath).exists())) {
            log.debug "Creating new Thumbnail dir: ${thumbsPath}"
            new File(thumbsPath).mkdirs()
        }

        def thumbnail = "${thumbsPath}${entry.id}.jpg"
        def thumbnailBig = "${thumbsPath}${entry.id}-orig.jpg"

        log.info "file path is $thumbnailBig"

        if (!(new File(thumbnail).exists())) {

            if (writeThumb) {
                writeFile(entry.link, thumbnailBig, thumbnail)
            } else {
                // I don't have it, and can't fetch it, but I've got to return something
                return new byte[0]
            }

        } else {
            log.info "Already got that image in the filesystem..."
        }


        File file = smallSize ? new File(thumbnail) : new File(thumbnailBig)
        if (file.exists()) {
            byte[] b = file.readBytes()

            if (b.length) {
                // put it in the cache for next time...
                cacheService.putToCache("thumbFileCache", 3600, "${id}-${smallSize}", b)
            }

            return b
        } else {
            return new byte[0]
        }

    }
    */

}
