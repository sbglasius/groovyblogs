
import grails.test.*

class TranslateServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
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
      def lang = translateService.getLanguage(new URL("http://www.google.es/"))
      assertEquals "es", lang

    }
}
