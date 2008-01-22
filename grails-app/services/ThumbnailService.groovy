import java.io.FileOutputStream;
import java.io.File;

import groovy.net.xmlrpc.XMLRPCServerProxy;

import org.codehaus.groovy.grails.commons.ConfigurationHolder;


/**
 * @author Glen Smith
 */
public class ThumbnailService {

    public void writeFile(String url, String thumbpathBig, String thumbpath) {

		log.warn("Staring download on $url ...")

		def server = new XMLRPCServerProxy(ConfigurationHolder.config.thumbnail.serviceurl)

		def list = server.getThumbnail(url)

		FileOutputStream big = new FileOutputStream(thumbpathBig)
		big << list[0]

		FileOutputStream small = new FileOutputStream(thumbpath)
		small << list[1]

	}

    public byte[] getFile(String id, boolean smallSize, boolean writeThumb) {

        if (!ConfigurationHolder.config.thumbnail.enabled) {
            log.debug("Thumbnail service disabled")
            return new byte[0]
        }

        log.info "Fetching thumbnail for id $id"

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
			log.info "Already got that image in the cache..."
		}


		File file = smallSize ? new File(thumbnail) : new File(thumbnailBig)
		byte[] b = file.readBytes()
		return b

	}

}
