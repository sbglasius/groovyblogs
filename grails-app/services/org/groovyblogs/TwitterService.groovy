package org.groovyblogs

import twitter4j.Twitter

class TwitterService {

    static transactional = false

    void sendTweet(String tweet) {

        def user = SystemConfig.findBySettingName("twitter.user")?.settingValue
        def pw = SystemConfig.findBySettingName("twitter.password")?.settingValue

        log.info "Sending tweet: [${tweet}] for user [${user}]"
        new Twitter(user, pw).updateStatus(tweet)
        log.debug "Tweet completed successfully"
    }
}
