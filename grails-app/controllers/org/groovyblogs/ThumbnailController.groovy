package org.groovyblogs
import grails.plugin.springsecurity.annotation.Secured

import javax.servlet.http.HttpServletResponse

@Secured(['permitAll'])
class ThumbnailController {

    ThumbnailService thumbnailService

    def index() {
        redirect(action: 'show')
    }


    private void writeImage(String id, String imgSize) {

        byte[] b = thumbnailService.getFile(id, imgSize)
        if(b?.size() > 10) {
            response.setContentType("image/jpeg")
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        } else {
            response.sendError HttpServletResponse.SC_NOT_FOUND
        }

    }

    def show(String id) {

        writeImage(id,  "small")

    }

    def showLarge(String id) {

        writeImage(id, "large")

    }


}

