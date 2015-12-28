package org.groovyblogs.config

import groovy.transform.CompileStatic
import org.grails.io.support.DefaultResourceLoader
import org.grails.io.support.Resource
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource

@CompileStatic
trait ExternalConfig implements EnvironmentAware {
    /**
     * Set the {@code Environment} that this object runs in.
     */
    @Override
    void setEnvironment(Environment environment) {
        List locations = environment.getProperty('grails.config.locations', ArrayList) as List
        if (locations) {
                def resourceLoader = new DefaultResourceLoader()
            locations.each { location ->
                Resource resource = resourceLoader.getResource(location instanceof Class ? "${((Class)location).name}.groovy".toString() : location.toString())
                print "Config ${resource.filename}"
                if(resource.exists()) {
                    print " ...loading"
                    def config = new ConfigSlurper(grails.util.Environment.current.name).parse(resource.URL)
                    ((AbstractEnvironment)environment).propertySources.addFirst(new MapPropertySource(config.toString(), config.flatten()))
                    println " ...done"
                } else {
                    println " ...not found"
                }
            }
        }

    }
}
