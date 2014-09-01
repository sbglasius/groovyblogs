class SearchController {
	
    def index () {
        redirect(action:search,params:params)
    }

    def search () {
        def query = params.query

        if (!query) {
            return [:]
        }

        try {
            def searchResult = BlogEntry.search(query, params)
		    println "\n\n\n${searchResult.dump()}\n\n\n"
            return [searchResult: searchResult]
        } catch (Exception e) {
            return [searchError: true]
        }
    }

}

