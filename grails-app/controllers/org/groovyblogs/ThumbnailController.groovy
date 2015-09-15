package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

import javax.servlet.http.HttpServletResponse

@Secured(['permitAll'])
class ThumbnailController {

    ThumbnailService thumbnailService

    static defaultAction = 'show'

    def show(BlogEntry blogEntry) {
        def image = thumbnailService.serveThumbnail(blogEntry)
        if (image) {
            response.contentType = "image/jpeg"
            response.contentLength = image.length
            response.outputStream.write(image)
        } else {
            response.status = HttpServletResponse.SC_NOT_FOUND
        }
    }

    def callback(String id, String key) {
        log.debug("Got callback for $key with $params")
        thumbnailService.processThumbnail(id, key)
        response.status = HttpServletResponse.SC_NOT_FOUND
    }
}
