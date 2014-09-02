package org.groovyblogs

import twitter4j.*

class TwitterService {

    static transactional = false

    def sendTweet(String tweet) {
	
		def user = org.groovyblogs.SystemConfig.findBySettingName("twitter.user")?.settingValue // ConfigurationHolder.config.twitter.user
        def pw =  org.groovyblogs.SystemConfig.findBySettingName("twitter.password")?.settingValue // ConfigurationHolder.config.twitter.password
		
		Twitter twitter = new Twitter(user, pw)
		log.info "Sending tweet: [${tweet}] for user [${user}]"
		twitter.updateStatus(tweet)	
		log.debug "Tweet completed successfully"

    }
}
