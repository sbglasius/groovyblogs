
import grails.test.GrailsUnitTestCase

class ThumbnailServiceTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
        loadCodec(org.codehaus.groovy.grails.plugins.codecs.MD5Codec)
        mockLogging(ThumbnailService.class, true)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testImageFetch() {

      mockConfig('''


thumbnail {
    enabled=true
    // user = your_user_id
    // apiKey = your_api_key
    endpointurl = "http://webthumb.bluga.net/easythumb.php"

}

''')
      ThumbnailService ts = new ThumbnailService()
      ts.thumbCache = new Expando()
      ts.thumbCache.put = { elementToCache ->
        println "Cached stuff"
      }
      ts.fetchThumbnailsToCache 1, "http://blogs.bytecode.com.au/glen"


    }
}
