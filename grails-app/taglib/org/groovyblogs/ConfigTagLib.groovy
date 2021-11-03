package org.groovyblogs

class ConfigTagLib {
    static defaultEncodeAs = [taglib: 'raw']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def ifEnabled = {attrs, body ->
        def enabled = grailsApplication.config.getProperty(attrs.config as String, Boolean)
        if(enabled) {
            out << body()
        }
    }

}
