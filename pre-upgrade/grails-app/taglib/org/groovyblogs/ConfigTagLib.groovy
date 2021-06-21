package org.groovyblogs

class ConfigTagLib {
    def grailsApplication
    static defaultEncodeAs = [taglib: 'raw']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def ifEnabled = {attrs, body ->
        def enabled = grailsApplication.flatConfig.get(attrs.config)
        if(enabled) {
            out << body()
        }
    }

}
