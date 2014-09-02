package org.groovyblogs

class ConfigService {

    def mail() {
        return [
                "host"   : "latte",
                "from"   : "glen@groovyblogs.org",
                "subject": "Welcome to groovyblogs.org",
                "enabled": true
        ]
    }

    def http() {
        return [
                "useproxy"       : false,
                "host"           : "192.168.1.7",
                "port"           : 3128,
                "useragent"      : "GroovyBlogs/1.0 (http://www.groovyblogs.org)",
                "usefeedburner"  : true,
                "feedburner_atom": "http://feeds.feedburner.com/groovyblogs",
                "feedburner_rss" : "http://feeds.feedburner.com/groovyblogs"
        ]
    }

    def lists() {
        return [
                "groovy": "http://groovy.329449.n5.nabble.com/groovy-user-f329450.xml",
                "grails": "http://grails.1312388.n4.nabble.com/Grails-user-f1312389.xml"
        ]

    }

    def translate() {
        return [
                "enabled": true,
                "url"    : 'http://translate.google.com/translate?u=${url}&langpair=${from}|${to}hl=${to}',
                // http://translate.google.com/translate?u=http%3A%2F%2Fgroovy.org.es%2Fhome%2Fstory%2F12&langpair=es%7Cen&hl=en&ie=UTF-8&oe=UTF-8&prev=%2Flanguage_tools
                "langs"  : [
                        "german"    : "de",
                        "french"    : "fr",
                        "spanish"   : "es",
                        "italian"   : "it",

                        "japanese"  : "ja",
                        "korean"    : "ko",
                        "portuguese": "pt",
                        "russian"   : "ru",
                        "arabic"    : "ar",
                        "chinese"   : "zh",

                ]
        ]

    }

}

