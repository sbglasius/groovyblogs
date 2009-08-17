import twitter4j.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TwitterService {

    boolean transactional = false

    def sendTweet(String tweet) {
	
		def user = ConfigurationHolder.config.twitter.user
        def pw = ConfigurationHolder.config.twitter.password
		
		Twitter twitter = new Twitter(user, pw)
		log.info "Sending tweet: [${tweet}] for user [${user}]"
		twitter.updateStatus(tweet)	
		log.debug "Tweet completed successfully"

    }
}
