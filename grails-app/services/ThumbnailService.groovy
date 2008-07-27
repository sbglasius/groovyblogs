import java.io.FileOutputStream;
import java.io.File;

import groovy.net.xmlrpc.XMLRPCServerProxy;

import org.codehaus.groovy.grails.commons.ConfigurationHolder;


/**
 * @author Glen Smith
 */
public class ThumbnailService {

    CacheService cacheService
    ThumbnailQueueService thumbnailQueueService

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

		def smallFilename = "${thumbsPath}${entry.id}.jpg"
		def bigFilename = "${thumbsPath}${entry.id}-orig.jpg"
		
		log.info "file path is $smallFilename"

		if (!(new File(smallFilename).exists())) {

            if (writeThumb) {
                if (ConfigurationHolder.config.mq.enabled) {
                    thumbnailQueueService.sendRequest(entry, smallFilename, bigFilename)        
                } else {
                    writeFile(entry.link, bigFilename, smallFilename)
                }
            } else {
                // I don't have it, and can't fetch it, but I've got to return something
                return new byte[0]
            }

		} else {
			log.info "Already got that image in the filesystem..."
		}


		File file = smallSize ? new File(smallFilename) : new File(bigFilename)
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

}
