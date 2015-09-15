package org.groovyblogs

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

class Application extends GrailsAutoConfiguration implements EnvironmentAware{
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {
        String configPath = System.properties["appname.config.location"]
        if(!configPath) {
            configPath = System.properties['user.home']
            configPath = configPath ? "${configPath}/.grails/groovyblogs-config.groovy" : null
        }
        if(!configPath) return
        Resource resourceConfig = new FileSystemResource(configPath);
        if(!resourceConfig.exists()) return
        Properties properties = new YamlPropertiesFactoryBean().with {
            resources = resourceConfig
            afterPropertiesSet()
            return object
        }

        environment.propertySources.addFirst(new PropertiesPropertySource("appname.config.location", properties))
    }
}