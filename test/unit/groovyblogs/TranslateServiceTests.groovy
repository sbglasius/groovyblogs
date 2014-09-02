package groovyblogs

import grails.test.GrailsUnitTestCase
import org.groovyblogs.TranslateService

class TranslateServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
        loadCodec(org.codehaus.groovy.grails.plugins.codecs.URLCodec)
        mockLogging(TranslateService.class, true)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTranslate() {

        mockConfig('''

translate {
    enabled=false
    langUrl="https://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q="
    url='http://translate.google.com/translate?hl=${to}&sl=auto&tl=${to}&u=${url}'
 
}

''')
        TranslateService translateService = new TranslateService()
        def lang = translateService.getLanguage("http://www.groovy.org.es/")
        assertEquals "es", lang

    }
}
