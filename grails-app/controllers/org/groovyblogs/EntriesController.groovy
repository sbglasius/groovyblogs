package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured
import groovy.text.SimpleTemplateEngine

import javax.servlet.http.HttpServletResponse

@Secured(['permitAll'])
class EntriesController {

    static final LinkedHashMap<Serializable, String> DAYS_AVAILABLE = [7: '7 Days', 14: '14 Days', 31: 'Month', 90: '3 Months', 182: '6 Months', 365: 'Year', (99999): 'like forever']
    static final int PAGE_LENGTH = 7
    FeedService feedService
    EntriesService entriesService

    static defaultAction = 'recent'

    def recent() {
        [
                pageTitle: "Recent Entries",
        ]
    }

    def recentNext(Integer page) {
        def entries = entriesService.getRecentEntries(PAGE_LENGTH, PAGE_LENGTH * (page ?: 0))
        render template: 'entries', model: [entries   : entries,
                                            thumbnails: grailsApplication.config.thumbnail.enabled]

    }

    def popular(Integer page, Integer days) {
        render view: 'recent',
                model: [
                        days          : days ?: 7,
                        selectableDays: DAYS_AVAILABLE,
                        pageTitle     : "Popular Entries (Last ${DAYS_AVAILABLE[days]})"]
    }

    def popularNext(Integer page, Integer days) {
        def entries = entriesService.getPopularEntries(days ?: 7, PAGE_LENGTH, PAGE_LENGTH * (page ?: 0))
        if (entries.size() == 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT)
        }
        render template: 'entries', model: [entries   : entries,
                                            thumbnails: grailsApplication.config.thumbnail.enabled]
    }

    def lists() {
        [entries: feedService.getCachedListEntries()]
    }

    def tweets() {
        [entries: feedService.getCachedTweetEntries()]
    }

    def jump(Long id) {
        BlogEntry blogEntry = entriesService.getEntry(id)
        if (!blogEntry) {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: 'recent')
        } else {
//            response.sendRedirect(blogEntry.link)
            [blogEntry: blogEntry]
        }
    }

    def jumpTranslate(Long id) {

        BlogEntry be = entriesService.getEntry(id)
        def lang = params.lang
        if (be && lang) {
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(grailsApplication.config.translate.url)
            def binding = [
                    from: lang,
                    to  : "en",
                    url : be.link.encodeAsURL(),
            ]
            String jumpTranslateUrl = template.make(binding)
            response.sendRedirect(jumpTranslateUrl)
        } else {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: 'recent')
        }
    }
}

