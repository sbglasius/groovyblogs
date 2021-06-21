package groovyblogs

import grails.test.GrailsUnitTestCase

import org.grails.plugins.codecs.URLCodec
import org.groovyblogs.TranslateService

class TranslateServiceTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
        loadCodec(URLCodec)
        mockLogging(TranslateService, true)
    }

    void testTranslate() {

        mockConfig('''

translate {
    enabled=false
    langUrl="https://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q="
    url='http://translate.google.com/translate?hl=${to}&sl=auto&tl=${to}&u=${url}'

}

''')
        def lang = new TranslateService().getLanguage("http://www.groovy.org.es/")
        assertEquals "es", lang

    }
}
