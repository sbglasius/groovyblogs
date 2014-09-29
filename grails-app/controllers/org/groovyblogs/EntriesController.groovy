package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured
import groovy.text.SimpleTemplateEngine

@Secured(['permitAll'])
class EntriesController {

    FeedService feedService
    EntriesService entriesService

    static defaultAction = 'recent'

    def recent(Integer days) {
        days = Math.min(days ?: EntriesService.DEFAULT_DAYS_TO_REPORT, 60) // default to 7 days and max 60 days

        def entries = entriesService.getRecentEntries(days)

        [entries:    entries,
         pageTitle: "Recent Entries (Last ${days} Days)",
         thumbnails: grailsApplication.config.thumbnail.enabled]
    }

    def endless() {
        params.offset = 0
        params.max = 3
        def entries = entriesService.getEndlessEntries(params)

        [entries: entriesService.limitEntries(entries),
         thumbnails: grailsApplication.config.thumbnail.enabled]
    }

    def endlessNext() {

        params.max = 3
        def entries = entriesService.getEndlessEntries(params)
        //TODO return fragment
        render template: 'entry',
               model: [
                   entries: entriesService.limitEntries(entries),
                   thumbnails: grailsApplication.config.thumbnail.enabled]
    }

    def popular() {

        def entries = entriesService.getPopularEntries()

        render view: 'recent',
               model: [entries: entries,
                       pageTitle: 'Popular Entries (Last 7 Days)',
                       thumbnails: grailsApplication.config.thumbnail.enabled]
    }

    def lists() {
        [entries: feedService.getCachedListEntries()]
    }

    def tweets() {
        [entries: feedService.getCachedTweetEntries()]
    }

    def jump(Long id) {
        BlogEntry be = entriesService.getEntry(id)
        if (be) {
            response.sendRedirect(be.link)
        } else {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: 'recent')
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

