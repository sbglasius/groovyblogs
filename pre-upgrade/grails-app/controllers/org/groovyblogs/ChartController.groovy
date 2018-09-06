package org.groovyblogs

import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class ChartController {

    static defaultAction = 'siteStats'
    def chartService

    def siteStats() {

        // grab chart bytes from cache if possible
        def cb = chartService.chart

        response.addHeader("Cache-Control", "max-age=60")

        response.contentType = "image/png"
        response.contentLength = cb.length
        response.outputStream.write(cb)
        response.outputStream.flush()

        // EncoderUtil.writeBufferedImage(chart.createBufferedImage(160, 130), "png", response.getOutputStream(), true)
    }
}
