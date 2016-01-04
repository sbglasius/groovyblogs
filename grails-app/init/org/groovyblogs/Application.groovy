package org.groovyblogs

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.groovyblogs.config.ExternalConfig

class Application extends GrailsAutoConfiguration implements ExternalConfig {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}