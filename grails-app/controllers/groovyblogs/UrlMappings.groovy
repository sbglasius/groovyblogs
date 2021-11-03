package groovyblogs

class UrlMappings {

    static mappings = {
        "/feed/atom"(controller: 'feed', action: 'atom')
        "/feed/rss"(controller: 'feed', action: 'rss')
        "/feed/$feedFormat"(controller: 'feed', action: 'otherFormat')
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }
        "/about"(view: '/about')
        "/"(controller: "entries")
        "500"(view: '/error')
        "404"(view: '/notFound')    }
}
