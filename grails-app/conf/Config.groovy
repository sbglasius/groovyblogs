// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

def catalinaBase = System.properties.getProperty('catalina.base')


grails.config.locations = ["classpath:${appName}-config.properties",
                           "classpath:${appName}-config.groovy",
                           "file:${userHome}/.grails/${appName}-config.properties",
                           "file:${userHome}/.grails/${appName}-config.groovy",
                           "file://${catalinaBase}/conf/${appName}-config.groovy"]

println "Expected config locations: ${grails.config.locations}"

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// from controllers:   def propValue = grailsApplication.config.my.property
// from services: ConfigurationHolder.config.my.custom.data
// import org.codehaus.groovy.grails.commons.ConfigurationHolder

// grails.config.locations = [ "file:/opt/groovyblogs/groovyblogs-config.properties" ]

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
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

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

mq {
    enabled = false
}

thumbnail {
    enabled = true
    // user = your_user_id
    // apiKey = your_api_key
    endpointurl = "http://webthumb.bluga.net/easythumb.php"
}

groovyblogs {
    maxErrors = 10
}

pdf {
    dir = System.properties["java.io.tmpdir"]
}

cache {
    enabled = true
}

feeds {
    ignoreFeedEntriesOlderThan = 30 // days
    moderate = true
    moderator_email = "glen@bytecode.com.au"
    // moderator_email = you@yourhost.com
}

grails.mail.host = "localhost"
grails.mail.default.from = "glen@bytecode.com.au"

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

// log4j configuration
log4j = {

    appenders {
        rollingFile name: "gb",
                file: "groovyblogs.log",
                maxFileSize: "10MB",
                layout: pattern(conversionPattern: '%d %p %c{2} %m%n')

    }

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails."web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails."web.mapping', // URL mapping
            //'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate'

    debug 'grails.app'

    // trace  gb: ['org.codehaus.groovy.grails.commons'] // Good for debugging bean creation issues

}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.groovyblogs.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.groovyblogs.UserRole'
grails.plugin.springsecurity.authority.className = 'org.groovyblogs.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/'              : ['permitAll'],
        '/index'         : ['permitAll'],
        '/index.gsp'     : ['permitAll'],
        '/assets/**'     : ['permitAll'],
        '/**/js/**'      : ['permitAll'],
        '/**/css/**'     : ['permitAll'],
        '/**/images/**'  : ['permitAll'],
        '/**/favicon.ico': ['permitAll'],
        '/dbconsole/**'  : ['ROLE_ADMIN'],

]
grails.plugin.springsecurity.roleHierarchy = '''
   ROLE_ADMIN > ROLE_MODERATOR
   ROLE_MODERATOR > ROLE_USER
'''

grails.assets.less.compile = 'less4j'
grails.assets.plugin."twitter-bootstrap".excludes = ["**/*.less"]
grails.assets.plugin."twitter-bootstrap".includes = ["bootstrap.less"]
grails.assets.plugin."font-awesome-resources".excludes = ['**/*.less']
grails.assets.plugin."font-awesome-resources".includes = ['**/font-awesome.less']

google.analytics.webPropertyID = "UA-54496952-1"
