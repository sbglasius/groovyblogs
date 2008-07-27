import org.springframework.context.* 

class UrlQueueService implements ApplicationContextAware {

    ApplicationContext applicationContext

    //FeedService feedService // will trip circular reference

    def circularFeedService

    static expose = ['jms']

    static destination = "urlResponse"

    def onMessage = { msg ->

        println "Got URL Response: ${msg.id} -> ${msg.url}"
        try {
            updateBlog(msg.id, msg.contents)
        } catch (Exception e) {
            log.error("Failed update of ${msg.url}", e)
        }

    }

    def sendRequest(Blog blog) {

        log.debug "Sending MQ Request for ${blog.feedUrl}"
        try {
            sendJMSMessage("urlRequest", [ url : blog.feedUrl, id : blog.id ])
        } catch (Exception e) {
             log.error("Failed to send MQ request for ${blog?.feedUrl}", e)
        }

    }

    def updateBlog(blogId, String contents) {

        Blog blog = Blog.get(blogId)
        if (blog) {
            println "Received HTML for ${blog.title}"
            if (!circularFeedService)
                circularFeedService = applicationContext?.getBean("feedService")
            println "Feedservice ${circularFeedService}"
            circularFeedService.updateFeedFromHtml(blogId, contents)
            println "Finished updated of ${blog.title}"
        }

    }
}
