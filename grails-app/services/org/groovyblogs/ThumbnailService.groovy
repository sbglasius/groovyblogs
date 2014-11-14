package org.groovyblogs

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat

import javax.imageio.ImageIO

import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.GetMethod

/**
 * @author Glen Smith
 */
class ThumbnailService {

    def grailsApplication
    def thumbCache
    Ehcache pendingCache

    void fetchThumbnailsToCache(long id, String url) {

        byte[] small = fetchThumbnail(url, "medium")
        String smallCacheEntry = "${id}-small"
        thumbCache.put(new Element(smallCacheEntry, small))

        byte[] large = fetchThumbnail(url, "medium2")
        String largeCacheEntry = "${id}-large"
        thumbCache.put(new Element(largeCacheEntry, large))
    }

    byte[] fetchThumbnail(String url, String imgSize = "large") {

        log.debug "Fetching remote thumbnail for ${url} of size ${imgSize}"
        def sdf = new SimpleDateFormat("yyyyMMdd")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        def date = sdf.format(new Date())

        def user = SystemConfig.findBySettingName("thumbnail.user")?.settingValue // Holders.config.thumbnail.user
        def apiKey = SystemConfig.findBySettingName("thumbnail.apiKey")?.settingValue // Holders.config.thumbnail.apiKey
        log.debug("Setting date to ${date}")

        def stringToHash = "${date}${url}${apiKey}"
        def hash = stringToHash.encodeAsMD5()
        log.debug("Hash is ${hash} of ${date}${url}${apiKey}")

        GetMethod get = new GetMethod(grailsApplication.config.thumbnail.endpointurl)
        NameValuePair[] nvp = [
                new NameValuePair("user", user.toString()),
                new NameValuePair("url", url.toString()),
                new NameValuePair("size", imgSize.toString()),
                new NameValuePair("cache", "7"),
                new NameValuePair("hash", hash.toString())
        ]
        get.setQueryString(nvp)

        HttpClient httpclient = new HttpClient()

        if (grailsApplication.config.http.useproxy) {
            def hostConfig = httpclient.getHostConfiguration()
            hostConfig.setProxy(grailsApplication.config.http.host, grailsApplication.config.http.port as int)
            log.warn("Setting proxy to [$grailsApplication.config.http.host]")
        }

        httpclient.executeMethod(get)
        byte[] image = get.getResponseBody(1024 * 100)
        log.debug "Fetch of ${url} complete"
        return image
    }

    // image scaling stuff from http://www.velocityreviews.com/forums/t148931-how-to-resize-a-jpg-image-file.html
    byte[] scale(byte[] srcFile, int destWidth, int destHeight) throws IOException {
        BufferedImage src = ImageIO.read(new ByteArrayInputStream(srcFile))
        BufferedImage dest = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB)
        Graphics2D g = dest.createGraphics()
        AffineTransform at = AffineTransform.getScaleInstance(
                (double) destWidth / src.getWidth(),
                (double) destHeight / src.getHeight())
        g.drawRenderedImage(src, at)
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(dest, "JPG", baos)
        return baos.toByteArray()
    }

    byte[] getFile(String id, String thumbSize) {

        if (!grailsApplication.config.thumbnail.enabled) {
            log.debug("Thumbnail service disabled")
            return new byte[0]
        }


        String cacheEntry = "${id}-${thumbSize}"
        byte[] t = thumbCache.get(cacheEntry)?.value
        if (t && t.length > 10) {  // Could be "Bad Hash"
            return t
        }

        BlogEntry entry = BlogEntry.get(id)
        //log.debug entry.dump()
        pendingCache.put(new Element(entry.link, entry.id))

        // thumbCache.put(new Element(cacheEntry, image))
        return null
    }
}
