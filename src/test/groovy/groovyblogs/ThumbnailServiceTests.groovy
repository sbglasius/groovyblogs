package groovyblogs

import grails.testing.web.GrailsWebUnitTest
import groovy.test.GroovyTestCase
import org.grails.testing.GrailsUnitTest
import org.groovyblogs.ThumbnailService

class ThumbnailServiceTests extends GroovyTestCase implements GrailsWebUnitTest {

    protected void setUp() {
        super.setUp()
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
