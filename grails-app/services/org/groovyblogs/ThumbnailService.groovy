package org.groovyblogs

import grails.util.Holders
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.GetMethod

import javax.imageio.ImageIO
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

/**
 * @author Glen Smith
 */
public class ThumbnailService {

    def thumbCache
    Ehcache pendingCache
    byte[] noThumbAvailablePic

    public void fetchThumbnailsToCache(long id, String url) {

        byte[] small = fetchThumbnail(url, "medium")
        def smallCacheEntry = "${id}-small".toString()
        thumbCache.put(new Element(smallCacheEntry, small))

        byte[] large = fetchThumbnail(url, "medium2")
        def largeCacheEntry = "${id}-large".toString()
        thumbCache.put(new Element(largeCacheEntry, large))

        /*
        byte[] iphone = fetchThumbnail(url, "small")
        def iphoneCacheEntry = "${id}-iphone".toString()
        thumbCache.put(new Element(iphoneCacheEntry, iphone))
        */


    }

    public byte[] fetchThumbnail(String url, String imgSize = "large") {

        log.debug "Fetching remote thumbnail for ${url} of size ${imgSize}"
        def sdf = new java.text.SimpleDateFormat("yyyyMMdd")
        TimeZone tz = TimeZone.getTimeZone("GMT")
        sdf.setTimeZone(tz)
        def date = sdf.format(new Date())

        def user = SystemConfig.findBySettingName("thumbnail.user")?.settingValue // Holders.config.thumbnail.user
        def apiKey = SystemConfig.findBySettingName("thumbnail.apiKey")?.settingValue // Holders.config.thumbnail.apiKey
        log.debug("Setting date to ${date}")

        def stringToHash = "${date}${url}${apiKey}"
        def hash = stringToHash.encodeAsMD5()
        log.debug("Hash is ${hash} of ${date}${url}${apiKey}")

        GetMethod get = new GetMethod(Holders.config.thumbnail.endpointurl)
        def nvp = [
                new NameValuePair("user", user.toString()),
                new NameValuePair("url", url.toString()),
                new NameValuePair("size", imgSize.toString()),
                new NameValuePair("cache", "7"),
                new NameValuePair("hash", hash.toString())
        ]
        get.setQueryString(nvp as NameValuePair[])

        HttpClient httpclient = new HttpClient()

        if (Holders.config.http.useproxy) {
            def hostConfig = httpclient.getHostConfiguration()
            hostConfig.setProxy(Holders.config.http.host, Holders.config.http.port as int)
            log.warn("Setting proxy to [" + Holders.config.http.host + "]")
        }

        httpclient.executeMethod(get)
        byte[] image = get.getResponseBody(1024 * 100)
        log.debug "Fetch of ${url} complete"
        return image

    }

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

        if (!Holders.config.thumbnail.enabled) {
            log.debug("Thumbnail service disabled")
            return new byte[0]
        }

        log.info "Fetching thumbnail for id $id"

        def cacheEntry = "${id}-${thumbSize}".toString()
        byte[] t = thumbCache.get(cacheEntry)?.value
        if (t && t.length > 10) {  // Could be "Bad Hash"
            log.debug "Found file in the cache..."
            return t
        }

        BlogEntry entry = BlogEntry.get(id)
        //log.debug entry.dump()
        def image
        try {

            pendingCache.put(new Element(entry.link, entry.id))

            if(true) return null
            if (!noThumbAvailablePic) {
                def noThumbUrl = this.class.getResource("/resources/no-thumb.jpg")
                log.debug "The noThumb image is ${noThumbUrl}"
                noThumbAvailablePic = new File(noThumbUrl.toURI()).readBytes()


            }
            image = noThumbAvailablePic

            switch (thumbSize) {
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

        } catch (Exception e) {
            log.warn "Thumb retrieval failed for ${id}", e
            image = new byte[0]
        }
        // thumbCache.put(new Element(cacheEntry, image))
        return image

    }


}
