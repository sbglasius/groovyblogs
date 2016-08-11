import org.groovyblogs.UserService
import org.hibernate.dialect.MySQL5InnoDBDialect

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

try {
    def catalinaBase = System.properties.getProperty('catalina.base')

    grails.config.locations = [
            "classpath:groovyblogs-config.properties",
            "classpath:groovyblogs-config.groovy",
            "file://${userHome}/.grails/groovyblogs-config.properties",
            "file://${userHome}/.grails/groovyblogs-config.groovy",
            "file://${catalinaBase}/conf/groovyblogs-config.groovy"]

    grails.mime.types = [
            all          : '*/*',
            atom         : 'application/atom+xml',
            css          : 'text/css',
            csv          : 'text/csv',
            form         : 'application/x-www-form-urlencoded',
            html         : ['text/html', 'application/xhtml+xml'],
            js           : 'text/javascript',
            json         : ['application/json', 'text/json'],
            multipartForm: 'multipart/form-data',
            rss          : 'application/rss+xml',
            text         : 'text/plain',
            hal          : ['application/hal+json', 'application/hal+xml'],
            xml          : ['text/xml', 'application/xml']
    ]

    thumbnail {
        enabled = false
        // user = your_user_id
        // apiKey = your_api_key
        endpointurl = "http://webthumb.bluga.net/easythumb.php"
    }

    groovyblogs {
        maxErrors = 10
        login = true
    }


    feeds {
        ignoreFeedEntriesOlderThan = 120 // days
        moderate = true
        moderator_email = "sbglasius@groovyblogs.org"
        // moderator_email = you@yourhost.com
    }

    http {
        timeout = 10 * 1000
        useragent = "GroovyBlogs/1.2 (http://www.groovyblogs.org)"
        usefeedburner = true
        maxpollsperminute = 7
        feedburner_atom = "http://feeds.feedburner.com/groovyblogs"
        feedburner_rss = "http://feeds.feedburner.com/groovyblogs"
        feedburner_stats_url = "http://feeds.feedburner.com/~fc/groovyblogs?bg=99CCFF&amp;fg=444444&amp;anim=0"
    }

    lists {
        groovy = "http://groovy.329449.n5.nabble.com/groovy-user-f329450.xml"
        grails = "http://grails.1312388.n4.nabble.com/Grails-user-f1312389.xml"
    }

    tweets {
        enabled = false
        url = "http://feeds.groovytweets.org/latestgroovytweets"
    }

    translate {
        enabled = false
        langUrl = "https://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q="
        //langUrl="http://www.google.com/uds/GlangDetect?v=1.0&q="
        // apikey = yourkey
        url = 'http://translate.google.com/translate?hl=${to}&sl=auto&tl=${to}&u=${url}'
        // http://translate.google.com/translate?u=http%3A%2F%2Fgroovy.org.es%2Fhome%2Fstory%2F12&langpair=es%7Cen&hl=en&ie=UTF-8&oe=UTF-8&prev=%2Flanguage_tools


    }
    environments {
        development {
            http.usefeedburner = false
        }
    }

    grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.groovyblogs.User'
    grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.groovyblogs.UserRole'
    grails.plugin.springsecurity.authority.className = 'org.groovyblogs.Role'
    grails.plugin.springsecurity.controllerAnnotations.staticRules = [
            [pattern: '/'                 , access: ['permitAll']],
            [pattern: '/about'            , access: ['permitAll']],
            [pattern: '/searchable/**'    , access: ['permitAll']],
            [pattern: '/index'            , access: ['permitAll']],
            [pattern: '/index*'           , access: ['permitAll']],
            [pattern: '/assets/**'        , access: ['permitAll']],
            [pattern: '/**/js/**'         , access: ['permitAll']],
            [pattern: '/**/css/**'        , access: ['permitAll']],
            [pattern: '/**/images/**'     , access: ['permitAll']],
            [pattern: '/**/favicon.ico'   , access: ['permitAll']],
            [pattern: '/dbconsole/**'     , access: ['ROLE_ADMIN']],
            [pattern: '/quartz/**'        , access: ['ROLE_ADMIN']],
            [pattern: '/runtimeLogging/**', access: ['ROLE_ADMIN']],
            [pattern: '/greenmail/**'     , access: ['permitAll']]

    ]
    grails.plugin.springsecurity.roleHierarchy = '''
   ROLE_ADMIN > ROLE_MODERATOR
   ROLE_MODERATOR > ROLE_USER
'''

    grails.plugins.twitterbootstrap.fixtaglib = true

    grails.assets.less.compile = 'less4j'

    google.analytics.webPropertyID = "UA-54496952-1"

    grails {
        cache {
            order = 2000 // higher than default (1000) and plugins, usually 1500
            enabled = true
            clearAtStartup = true // reset caches when redeploying
            ehcache {
                // ehcacheXmlLocation = 'classpath:ehcache.xml' // no custom xml config location (no xml at all)
                reloadable = false
            }
        }
    }

    def uniqueCacheManagerName = appName + "ConfigEhcache-" + System.currentTimeMillis()

// customize temp ehcache cache manager name upon startup
    grails.cache.ehcache.cacheManagerName = uniqueCacheManagerName


    grails.cache.config = {
        provider {
            updateCheck false
            monitoring 'on'
            dynamicConfig false
            // unique name when configuring caches
            name uniqueCacheManagerName
        }
        defaultCache {
            maxElementsInMemory 10000
            eternal false
            timeToIdleSeconds 120
            timeToLiveSeconds 120
            overflowToDisk false // no disk use, this would require more config
            maxElementsOnDisk 10000000
            diskPersistent false
            diskExpiryThreadIntervalSeconds 120
            memoryStoreEvictionPolicy 'LRU' // least recently used gets kicked out
        }

        cache {
            name 'recentList'
            timeToLiveSeconds 60

        }
        cache {
            name 'popularList'
            timeToLiveSeconds 60
        }

        cache {
            name 'chart'
            timeToLiveSeconds 60
        }


        cache {
            name UserService.TOKENS_CACHE
            timeToLiveSeconds 24 * 60 * 60
        }
    }
    quartz {
        jdbcStore = false
    }
    environments {
        development {
            grails.mail.port = com.icegreen.greenmail.util.ServerSetupTest.SMTP.port
            quartz {
                autoStartup = true
                pluginEnabled = true
            }
        }
        test {
            quartz {
                autoStartup = false
            }
        }
        production {
            grails.mail.host = "localhost"
            grails.mail.default.from = "sbglasius@groovyblogs.org"
            greenmail.disabled = true
            pluginEnabled = true

        }
    }

    recaptcha {
        // These keys are generated by the ReCaptcha service
        publicKey = ""   // Externalized config
        privateKey = ""  // Externalized config

        // Include the noscript tags in the generated captcha
        includeNoScript = true

        // Force language change. See this for more: http://code.google.com/p/recaptcha/issues/detail?id=133
        forceLanguageInURL = false

        // Set to false to disable the display of captcha
        enabled = true

        // Communicate using HTTPS
        useSecureAPI = true
    }

    dataSource {
        pooled = true
        jmxExport = true
    }
    hibernate {
        cache.use_second_level_cache = false
        cache.use_query_cache = false
//    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory'
//        cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
        cache.region.factory_class = 'grails.plugin.cache.ehcache.hibernate.BeanEhcacheRegionFactory4' // For EhCache method caching + Hibernate 4.0 and higher
        singleSession = true // configure OSIV singleSession mode
        flush.mode = 'manual' // OSIV session flush mode outside of transactional context
    }

    environments {
        development {
            dataSource {
                dbCreate = "update"
                url = "jdbc:mysql://localhost:3306/groovyblogs_dev?useUnicode=true&characterEncoding=UTF-8"
                driverClassName = "com.mysql.jdbc.Driver"
                dialect = MySQL5InnoDBDialect
                username = "root"
                password = "root"
            }
        }
        test {
            dataSource {
                dbCreate = "update"
                driverClassName = "org.h2.Driver"
                username = "sa"
                password = ""
                url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
            }
        }
    }


}
catch (Exception ex) {
    ex.printStackTrace()
}
