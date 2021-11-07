/**
 * Configuration of module test environment.
 * Neither application.groovy nor application.yml will be included in published module artefact!
 *
 * application.yml loads first, then loads application.groovy (and overwrites any) - last loaded configuration wins
 *
 * application.yml is based on Grails 3/4 because Spring uses yml - yml configuration should not be affected for future upgrades. #Vanilla
 * For backwards compatibility, Grails 3/4 supports application.groovy - this is used when upgrading thus minimizing the amount of changes.
 */

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [
        "~/.grails/groovyblogs-config.yml",
        "~/.grails/groovyblogs-config.groovy",
//        "/opt/groovyblogs/groovyblogs-config.yml"
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

cache {
    enabled = true
}

feeds {
    ignoreFeedEntriesOlderThan = 30 // days
    moderate = true
    moderator_email = "sbglasius@groovyblogs.org"
    // moderator_email = you@yourhost.com
}

http {
    /*
    useproxy=true
    host="192.168.1.7"
    port=3128
    */
    timeout = 10 * 1000
    useragent = "GroovyBlogs/1.2 (http://www.groovyblogs.org)"
    usefeedburner = true
    maxpollsperminute = 7
    feedburner_atom = "http://feeds.feedburner.com/groovyblogs"
    feedburner_rss = "http://feeds.feedburner.com/groovyblogs"
    feedburner_stats_url = "http://feeds.feedburner.com/~fc/groovyblogs?bg=99CCFF&amp;fg=444444&amp;anim=0"
}

lists {
    groovyUser = "https://mail-archives.apache.org/mod_mbox/groovy-users/?format=atom"
    groovyDev = "https://mail-archives.apache.org/mod_mbox/groovy-dev/?format=atom"
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
       [pattern: '/index.gsp'        , access: ['permitAll']],
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
grails.assets.plugin."twitter-bootstrap".excludes = ["**/*.less"]
grails.assets.plugin."twitter-bootstrap".includes = ["bootstrap.less"]
grails.assets.plugin."font-awesome-resources".excludes = ['**/*.less']
grails.assets.plugin."font-awesome-resources".includes = ['**/font-awesome.less']

google.analytics.webPropertyID = "UA-54496952-1"

grails.cache.config = {
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
        name 'tokens'
        timeToLiveSeconds 24 * 60 * 60
    }

    defaults {
        maxElementsInMemory 1000
        eternal false
        overflowToDisk false
        maxElementsOnDisk 0
    }
}

environments {
    development {
        grails.mail.port = com.icegreen.greenmail.util.ServerSetupTest.SMTP.port
        quartz {
            autoStartup = true
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



