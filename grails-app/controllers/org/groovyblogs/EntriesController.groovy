package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class EntriesController {

    FeedService feedService
    EntriesService entriesService

    def index() { redirect(action: 'recent', params: params) }

    def recent(Integer days) {
        days = Math.min(days ?: EntriesService.DEFAULT_DAYS_TO_REPORT, 60) // default to 7 days and max 60 days

        def entries = entriesService.getRecentEntries(days)
        return [
                entries   : entries,
                pageTitle : "Recent Entries (Last ${days} Days)",
                thumbnails: grailsApplication.config.thumbnail.enabled
        ]
    }

    def endless() {

        params.offset = 0
        params.max = 3
        def entries = entriesService.getEndlessEntries(params)
        [
                entries   : entriesService.limitEntries(entries),
                thumbnails: grailsApplication.config.thumbnail.enabled
        ]
    }

    def endlessNext() {

        params.max = 3
        def entries = entriesService.getEndlessEntries(params)
        //TODO return fragment
        render(template: 'entry', model: [
                entries   : entriesService.limitEntries(entries),
                thumbnails: grailsApplication.config.thumbnail.enabled])

    }


    def popular() {

        def entries = entriesService.getPopularEntries()

        render(view: 'recent',
                model: [entries   : entries,
                        pageTitle : 'Popular Entries (Last 7 Days)',
                        thumbnails: grailsApplication.config.thumbnail.enabled]
        )
    }

    def lists() {
        def entries = feedService.getCachedListEntries()
        render(view: 'lists',
                model: ['entries': entries])
    }

    def tweets() {
        def entries = feedService.getCachedTweetEntries()
        [entries: entries]
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
            def engine = new groovy.text.SimpleTemplateEngine()
            def template = engine.createTemplate(grailsApplication.config.translate.url)
            def binding = [
                    from: lang,
                    to  : "en",
                    url : be.link.encodeAsURL(),
            ]
            def jumpTranslateUrl = template.make(binding).toString()

            response.sendRedirect(jumpTranslateUrl)
        } else {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: 'recent')
        }

    }
}

