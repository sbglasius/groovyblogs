// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// from controllers:   def propValue = grailsApplication.config.my.property
// from services: ConfigurationHolder.config.my.custom.data
// import org.codehaus.groovy.grails.commons.ConfigurationHolder

grails.config.locations = [ "classpath:groovyblogs-config.properties" ]

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format

grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
    xml: ['text/xml', 'application/xml'],
    text: 'text-plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: ['application/json','text/json'],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data'
]

// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// grails.mail.host=yourhost.com
// grails.mail.default.from=you@yourhost.com


mq {
    enabled = false
}

thumbnail {
    enabled=true
    // user = your_user_id
    // apiKey = your_api_key
    endpointurl = "http://webthumb.bluga.net/easythumb.php"
}

pdf {
    dir=System.properties["java.io.tmpdir"]
}

cache {
    enabled = true
}

feeds {
    ignoreFeedEntriesOlderThan = 30 // days
    moderate = true
    // moderator_email = you@yourhost.com
}



http {
	/*
    useproxy=true
    host="192.168.1.7"
    port=3128
    */
    timeout=10*1000
    useragent="GroovyBlogs/1.2 (http://www.groovyblogs.org)"
    usefeedburner=true
    maxpollsperminute=7
    feedburner_atom="http://feeds.feedburner.com/groovyblogs"
    feedburner_rss="http://feeds.feedburner.com/groovyblogs"
    feedburner_stats_url="http://feeds.feedburner.com/~fc/groovyblogs?bg=99CCFF&amp;fg=444444&amp;anim=0"
}

lists {
    groovy="http://www.nabble.com/groovy---user-f11867.xml"
    grails="http://www.nabble.com/grails---user-f11861.xml"
}

tweets.url = "http://feeds.groovytweets.org/latestgroovytweets"

translate {
    enabled=true
    langUrl="http://www.google.com/uds/GlangDetect?v=1.0&q="
    // apikey = yourkey
    url='http://translate.google.com/translate?hl=${to}&sl=auto&tl=${to}&u=${url}'
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

    error  gb:[ 'org.codehaus.groovy.grails.web.servlet',  //  controllers
	       'org.codehaus.groovy.grails.web.pages', //  GSP
	       'org.codehaus.groovy.grails.web.sitemesh', //  layouts
	       'org.codehaus.groovy.grails."web.mapping.filter', // URL mapping
	       'org.codehaus.groovy.grails."web.mapping', // URL mapping
	       'org.codehaus.groovy.grails.commons', // core / classloading
	       'org.codehaus.groovy.grails.plugins', // plugins
	       'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
	       'org.springframework',
	       'org.hibernate' ]

    debug  gb: 'grails.app'
    
}


