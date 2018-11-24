package org.groovyblogs

import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Environment
import groovy.sql.Sql
import org.groovyblogs.Role
import org.groovyblogs.SystemConfig
import org.groovyblogs.Tag
import org.groovyblogs.User
import org.groovyblogs.UserRole

import javax.sql.DataSource

class BootStrap {
    GrailsApplication grailsApplication
    SpringSecurityService springSecurityService
    DataSource dataSource
    private static final List<String> TAGS = [
            'groovy', 'grails', 'griffon', 'gorm', 'gr8', 'gant',
            'gradle', 'gpars', 'gsp', 'geb', 'spock', 'gaelyk']

    private static final Map DEFAULT_CONFIG = [
            "thumbnail.user"  : "1234",
            "thumbnail.apiKey": "yourkey",

            "translate.apikey": "yourKey",

            "twitter.enabled" : "true",
            "twitter.user"    : "youruser",
            "twitter.password": "yourpassword"
    ]


    def init = { servletContext ->

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
            case Environment.PRODUCTION:
                createConfigurationIfRequired()
                createAdminUserIfRequired()
                createTagsIfRequired()
                break
        }
        updateSourceStatus()
    }

    void updateSourceStatus() {
        def sql = new Sql(dataSource)
        sql.execute("UPDATE blog_entry SET SOURCE_STATUS=200 WHERE SOURCE_STATUS=0")
    }

    void createTagsIfRequired() {
        if (Tag.count()) {
            return
        }

        for (String tag in TAGS) {
            new Tag(tag: tag, approved: true).save(failOnError: true)
        }
    }

    void createConfigurationIfRequired() {

        if (SystemConfig.count()) {
            return
        }

        DEFAULT_CONFIG.each { key, value ->
            new SystemConfig(settingName: key, settingValue: value).save(failOnError: true)
        }
    }

    void createAdminUserIfRequired() {
        Map<String, String> config = grailsApplication.config.getProperty('org.groovyblogs', Map)
        def adminRole = Role.findOrSaveWhere(authority: 'ROLE_ADMIN').save(flush: true, failOnError: true)
        def userRole = Role.findOrSaveWhere(authority: 'ROLE_USER').save(flush: true, failOnError: true)

        def adminUser = User.findByUsername(config.adminUser)
        if (adminUser) {
            adminUser.password = config.adminPassword
            adminUser.markDirty('password')
        } else {
            adminUser = new User(username: config.adminUser, password: config.adminPassword, email: "admin@groovyblogs.org")
        }
        adminUser.save(flush: true, failOnError: true)

        if (!UserRole.exists(adminUser.id, adminRole.id)) {
            UserRole.create(adminUser, adminRole, true)
        }
    }

}
