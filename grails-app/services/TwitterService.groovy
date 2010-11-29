import twitter4j.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TwitterService {

    static transactional = false

    def sendTweet(String tweet) {
	
		def user = SystemConfig.findBySettingName("twitter.user")?.settingValue // ConfigurationHolder.config.twitter.user
        def pw =  SystemConfig.findBySettingName("twitter.password")?.settingValue // ConfigurationHolder.config.twitter.password
		
		Twitter twitter = new Twitter(user, pw)
		log.info "Sending tweet: [${tweet}] for user [${user}]"
		twitter.updateStatus(tweet)	
		log.debug "Tweet completed successfully"

    }
}
