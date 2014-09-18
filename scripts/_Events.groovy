import groovy.xml.StreamingMarkupBuilder
import grails.util.Environment

// See http://refactor.com.au/blog/idiots-guide-tomcat-6-grails-jndi-datasource

if (Environment.current == Environment.PRODUCTION) {
//	println "\n\nCreating JNDI Entries in Tomcat's web.xml descriptor\n\n"
//    eventWebXmlEnd = {String tmpfile ->
//        def root = new XmlSlurper().parse(webXmlFile)
//
//        // add the data source
//        root.appendNode {
//            'resource-ref'{
//                'description'('The JNDI Database resource for groovyblogs')
//                'res-ref-name'('jdbc/groovyblogs')
//                'res-type'('javax.sql.DataSource')
//                'res-auth'('Container')
//            }
//        }
//
//        webXmlFile.text = new StreamingMarkupBuilder().bind {
//            mkp.declareNamespace("": "http://java.sun.com/xml/ns/j2ee")
//            mkp.yield(root)
//        }
//    }
}