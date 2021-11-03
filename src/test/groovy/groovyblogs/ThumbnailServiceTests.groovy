package groovyblogs

import grails.test.GrailsUnitTestCase

import org.grails.plugins.codecs.MD5Codec
import org.groovyblogs.ThumbnailService

class ThumbnailServiceTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
        loadCodec(MD5Codec)
        mockLogging(ThumbnailService, true)
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
