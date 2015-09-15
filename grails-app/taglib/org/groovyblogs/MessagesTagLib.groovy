package org.groovyblogs

class MessagesTagLib {
    static namespace = "m"
    static defaultEncodeAs = [taglib: 'raw']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def flashMessage = { attrs ->
        out << g.render(template: '/templates/message')
    }

    def errors = { attrs ->
        out << g.render(template: '/templates/errors', model: attrs)
    }
}
