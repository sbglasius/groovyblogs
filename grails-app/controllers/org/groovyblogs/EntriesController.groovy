package org.groovyblogs

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Value

import javax.servlet.http.HttpServletResponse

@GrailsCompileStatic
@Secured(['permitAll'])
class EntriesController {

    static final Map<Integer, String> DAYS_AVAILABLE = [
            7      : '7 Days',
            14     : '14 Days',
            31     : 'Month',
            90     : '3 Months',
            182    : '6 Months',
            365    : 'Year',
            (99999): 'like forever',]

    static final int PAGE_LENGTH = 7

    EntriesService entriesService

    static defaultAction = 'recent'

    @Value('${thumbnail.enabled}')
    boolean thumbnailEnabled

    def recent() {
        [
                pageTitle: "Recent Entries",
        ]
    }

    def recentNext(Integer page) {
        def entries = entriesService.getRecentEntries(PAGE_LENGTH, PAGE_LENGTH * (page ?: 0))
        render template: 'entries', model: [entries   : entries,
                                            thumbnails: thumbnailEnabled]

    }

    def popular(Integer days) {
        render view: 'recent',
                model: [
                        days          : days ?: 7,
                        selectableDays: DAYS_AVAILABLE,
                        pageTitle     : "Popular Entries (Last ${DAYS_AVAILABLE[days]})"]
    }

    def popularNext(Integer page, Integer days) {
        Collection<BlogEntry> entries = entriesService.getPopularEntries(days ?: 7, PAGE_LENGTH, PAGE_LENGTH * (page ?: 0))
        if (entries.size() == 0) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT)
        }
        render template: 'entries', model: [entries   : entries,
                                            thumbnails: thumbnailEnabled]
    }

    def jump(Long id) {
        BlogEntry blogEntry = entriesService.getEntry(id)
        if (blogEntry) {
            [blogEntry: blogEntry]
        } else {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: 'recent')
        }
    }
}

