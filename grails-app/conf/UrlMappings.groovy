class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/about"(view: '/about')
        "/"(controller: "entries")
        "500"(view:'/error')
    }
}
