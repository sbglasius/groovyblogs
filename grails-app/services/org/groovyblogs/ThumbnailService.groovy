package org.groovyblogs
import com.commsen.jwebthumb.WebThumbFetchRequest
import com.commsen.jwebthumb.WebThumbJob
import com.commsen.jwebthumb.WebThumbRequest
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.springframework.beans.factory.InitializingBean
/**
 * @author Glen Smith
 */
class ThumbnailService implements InitializingBean {

    def grailsApplication
    def webThumbService
    def grailsEventsPublisher
    def grailsLinkGenerator

    Ehcache pendingCache
    private File root

    void requestThumbnail(BlogEntry blogEntry) {
        def key = blogEntry.title.encodeAsMD5()
        if(pendingCache.get(key)) {
            return
        }
        log.debug("Requesting thumbnail for blogEntry: $blogEntry")

        def url = grailsApplication.config.jwebthumb.callbackUrl ?: grailsLinkGenerator.link(controller: 'thumbnail', action: 'callback', absolute: true)

        WebThumbRequest request = new WebThumbRequest(blogEntry.link, WebThumbRequest.OutputType.jpg)
        request.notify = "$url?key=${key}"

        WebThumbJob job = webThumbService.sendRequest(request)
        pendingCache.put(new Element(key, new ThumbRequest(jobId: job.id, blogEntry: blogEntry)))
    }

    void processThumbnail(String jobId, String key) {
        def thumbRequest = pendingCache.get(key)?.objectValue as ThumbRequest
        if(thumbRequest?.jobId != jobId) {
            log.info("Discarding request for jobId: $jobId")
            pendingCache.remove(key)
            return
        }
        def blogEntry = thumbRequest.blogEntry
        log.debug("Processing thumbnail callback for $blogEntry")

        WebThumbFetchRequest fetchRequest = new WebThumbFetchRequest(jobId, WebThumbFetchRequest.Size.medium2)
        def image = webThumbService.fetch(fetchRequest)
        store(blogEntry, image)
        pendingCache.remove(key)
    }


    byte[] serveThumbnail(BlogEntry blogEntry) {
        if(!blogEntry) {
            return null
        }
        def thumb = retrieve(blogEntry)
        if(!thumb) {
            notify "thumbnail.request", blogEntry
            return null
        }
        log.trace("Serving image for blogEntry: $blogEntry")
        return thumb
    }

    private void store(BlogEntry blogEntry, byte[] bytes) {
        File file = getThumbFile(blogEntry)
        file.bytes = bytes
    }

    private byte[] retrieve(BlogEntry blogEntry) {
        File file = getThumbFile(blogEntry)
        return file.exists() ? file.bytes : null

    }

    private File getThumbFile(BlogEntry blogEntry) {
        def fileName = "${blogEntry.title.encodeAsMD5()}.jpg"
        File file = new File(root, fileName)
        return file
    }

    @Override
    void afterPropertiesSet() throws Exception {
        root = new File(grailsApplication.config.thumbcache)
        root.mkdirs()
        log.info("Thumbs root set to $root.absolutePath")
        on('thumbnail.request') { BlogEntry  blogEntry ->
            this.requestThumbnail(blogEntry)
        }
    }

    private static class ThumbRequest implements Serializable{
        String jobId
        BlogEntry blogEntry
    }
}
