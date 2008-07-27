import org.springframework.context.*

class ThumbnailQueueService {

    ApplicationContext applicationContext 

    static expose = ['jms']

    static destination = "thumbnailResponse"

    def onMessage = {msg ->
        println "Got Thumbnail Response: ${msg.url}"

        try {
            if (msg.smallImage.size()) {
                FileOutputStream small = new FileOutputStream(msg.smallFilename)
                small << msg.smallImage.decodeBase64()
            }

            if (msg.bigImage.size()) {
                FileOutputStream big = new FileOutputStream(msg.bigFilename)
                big << msg.bigImage.decodeBase64()
            }
        } catch (Throwable t) {
            log.error("Error writing thumbnail for ${msg.url}", t)
        }

    }

    def sendRequest(BlogEntry be, String smallFilename, String bigFilename) {

        try {
            sendJMSMessage("thumbnailRequest",
              [ id: be.id,
                url : be.link, 
                smallFilename: smallFilename,
                bigFilename: bigFilename ])
        } catch (Exception e) {
             log.error("Failed to send MQ request for ${be?.link}", e)
        }

    }
}
