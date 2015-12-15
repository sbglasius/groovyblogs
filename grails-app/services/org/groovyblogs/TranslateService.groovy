package org.groovyblogs

import grails.converters.JSON

// A simple translation service that uses Google APIs. Read about it
// at http://blogs.bytecode.com.au/glen/2009/07/30/getting-groovy-with-google-language-translation-apis.html
class TranslateService {

    static transactional = false

    def grailsApplication

    def getLanguage(String text) {

        if (text.size() > 200) {
            text = text[0..<200]
        }

        String url = grailsApplication.config.translate.langUrl + text.encodeAsURL() + "&key=" +
            SystemConfig.findBySettingName("translate.apikey")?.settingValue

        String translateResponse = url.toURL().text

        log.debug "Google translate responsed: ${translateResponse}"

        def response = JSON.parse(translateResponse)
        return response.responseData.language
    }
}
