grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://developer.jasig.org/repo/content/groups/m2-legacy/"
        mavenRepo "https://m2proxy.atlassian.com/repository/public/"
        
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile 'jfree:jfreechart:1.0.4'
        compile 'net.homeip.yusuke:twitter4j:2.0.10'
        compile 'rome:rome:1.0'
        compile 'commons-httpclient:commons-httpclient:3.1'
        compile 'spy:memcached:2.3.1'
        runtime 'postgresql:postgresql:8.2-507.jdbc3'
        
        // runtime 'mysql:mysql-connector-java:5.1.5'
    }

}
