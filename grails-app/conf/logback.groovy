import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
//    logger("org.springframework.boot", DEBUG)
}

[ 'grails.app.domain.org.groovyblogs',
        'grails.app.controllers.org.groovyblogs',
        'grails.app.services.org.groovyblogs',
        'grails.app.taglibs.org.groovyblogs',
        'grails.app.jobs.org.groovyblogs',
        'org.groovyblogs'
//        'org.springframework.cache'
].each {
    logger(it, DEBUG, ['STDOUT'], false)
}
