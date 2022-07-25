package org.groovyblogs

import com.rometools.rome.feed.synd.impl.Converters
import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class FeedController {

    FeedService feedService
    GrailsApplication grailsApplication

    static final SUPPORTED_FORMATS = new Converters().supportedFeedTypes.collect().sort()

    static defaultAction = 'atom'

    def rss() {
        if (useFeedburner) {
            response.sendRedirect(grailsApplication.config.getProperty('http.feedburner_rss'))
        } else {
            render(text: feedService.getFeedData("rss_2.0"), contentType: "text/xml", encoding: "UTF-8")
        }
    }

    def atom() {
        if (useFeedburner) {
            response.sendRedirect(grailsApplication.config.getProperty('http.feedburner_atom'))
        } else {
            render(text: feedService.getFeedData("atom_1.0"), contentType: "text/xml", encoding: "UTF-8")
        }
    }

    def otherFormat(String feedFormat) {
        if (feedFormat in SUPPORTED_FORMATS) {
            render(text: feedService.getFeedData(feedFormat), contentType: "text/xml", encoding: "UTF-8")
        } else {
            response.sendError(response.SC_BAD_REQUEST, "Supported formats are: ${SUPPORTED_FORMATS.join(', ')}")
        }
    }

    private boolean isUseFeedburner() {
        if (!grailsApplication.config.getProperty('http.usefeedburner')) {
            return false
        }

        def userAgent = request.getHeader("user-agent")
        if (userAgent && userAgent =~ /(?i)FeedBurner/) {
            log.info("Feedburner Agent Detected: [$userAgent]")
            return false
        }

        log.debug("Redirecting: [$userAgent] from [${request.remoteAddr}] to feedburner")
        return true
    }


}

