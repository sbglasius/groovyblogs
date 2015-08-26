import org.groovyblogs.UserService

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

def catalinaBase = System.properties.getProperty('catalina.base')

grails.config.locations = [
        "classpath:${appName}-config.properties",
        "classpath:${appName}-config.groovy",
        "file:${userHome}/.grails/${appName}-config.properties",
        "file:${userHome}/.grails/${appName}-config.groovy",
        "file://${catalinaBase}/conf/${appName}-config.groovy"]

println "Expected config locations: ${grails.config.locations}"

// grails.config.locations = [ "file:/opt/groovyblogs/groovyblogs-config.properties" ]

grails.controllers.defaultScope = 'singleton'
grails.converters.encoding = "UTF-8"
grails.enable.native2ascii = true
grails.exceptionresolver.params.exclude = ['password']
grails.hibernate.cache.queries = false
grails.hibernate.osiv.readonly = false
grails.hibernate.pass.readonly = false
grails.json.legacy.builder = false
grails.mail.host = "localhost"
grails.mail.default.from = "info@groovyblogs.org"
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
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
grails.project.groupId = appName
grails.scaffolding.templates.domainSuffix = 'Instance'
grails.spring.bean.packages = []
grails.views.default.codec = "html"
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml'
            codecs {
                expression = 'html'
                scriptlet = 'html'
                taglib = 'none'
                staticparts = 'none'
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}
grails.web.disable.multipart = false

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

// log4j configuration
log4j = {

    appenders {
        rollingFile name: "gb",
                file: "groovyblogs.log",
                maxFileSize: "10MB",
                layout: pattern(conversionPattern: '%d %p %c{2} %m%n')

    }

    error 'org.codehaus.groovy.grails',
            'org.springframework',
            'org.hibernate'

    debug 'grails.app.domain.org.groovyblogs',
            'grails.app.controllers.org.groovyblogs',
            'grails.app.services.org.groovyblogs',
            'grails.app.taglibs.org.groovyblogs',
            'grails.app.jobs.org.groovyblogs',
            'org.springframework.cache'

//     trace  'org.codehaus.groovy.grails.commons' // Good for debugging bean creation issues

}

grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.groovyblogs.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.groovyblogs.UserRole'
grails.plugin.springsecurity.authority.className = 'org.groovyblogs.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/'                 : ['permitAll'],
        '/about'            : ['permitAll'],
        '/searchable/**'    : ['permitAll'],
        '/index'            : ['permitAll'],
        '/index.gsp'        : ['permitAll'],
        '/assets/**'        : ['permitAll'],
        '/**/js/**'         : ['permitAll'],
        '/**/css/**'        : ['permitAll'],
        '/**/images/**'     : ['permitAll'],
        '/**/favicon.ico'   : ['permitAll'],
        '/dbconsole/**'     : ['ROLE_ADMIN'],
        '/quartz/**'        : ['ROLE_ADMIN'],
        '/runtimeLogging/**': ['ROLE_ADMIN'],
        '/greenmail/**'     : ['permitAll']

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
        name UserService.TOKENS_CACHE
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



