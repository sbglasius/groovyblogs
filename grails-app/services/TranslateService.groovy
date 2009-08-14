import org.codehaus.groovy.grails.commons.ConfigurationHolder


// A simple translation service that uses Google APIs. Read about it
// at http://blogs.bytecode.com.au/glen/2009/07/30/getting-groovy-with-google-language-translation-apis.html
class TranslateService {

    boolean transactional = false

    def getLanguage(String text) {

        if (text.size() > 200) {
            text = text[0..<200]
        }

        String url = ConfigurationHolder.config.translate.langUrl +
            text.encodeAsURL() 
            // + "&key=" + ConfigurationHolder.config.translate.apikey

        def response =  grails.converters.JSON.parse(url.toURL().text)
        return response.responseData.language

    }
}
