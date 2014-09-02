package org.groovyblogs

class SearchTagLib {
	
    def searchBox = { attrs ->
        out << "<div id='searchBox'>"
        out << "<form action='"
		
        // from ApplicationTagLib
        out << grailsAttributes.getApplicationUri(request)
        // if the current attribute null set the controller uri to the current controller
        if(attrs["controller"]) {
            out << '/' << attrs.remove("controller")
        }
        else {
            out << grailsAttributes.getControllerUri(request)
        }
        if(attrs["action"]) {
            out << '/' << attrs.remove("action")
        }
		
        out << "' method='post'>"
		
        if (attrs.query == null) { attrs.query = "" }
		
        out << "<input id='searchField' size='45' name='query' value='${attrs.query}'/> "
		
		
        int selectedMaxHits
        if (params.hitcount) {
            selectedMaxHits = Integer.parseInt(params.hitcount)
        } else if (attrs.hitcount) {
            selectedMaxHits = Integer.parseInt(attrs.hitcount)
        } else {
            selectedMaxHits = 10
        }
		
        if (!attrs.noCombo) {
            out << "<select name='hitcount'>"
            for (i in [10,20,30,40,50]) {
                out << "<option value='$i' " + (i==selectedMaxHits ? "selected='selected' " : "") + ">$i hits</option>"
            }
            out << "</select> "
        }
		
        out << "<input type='hidden' name='fields' value='${attrs.fields}'/>"
        out << "<input id='searchButton' type='submit' value='Search'/>"
		
        out << "</form>"
        out << "</div>"
	
    }
	
    def searchResults = { attrs ->
	
        def searchResults = attrs.results
        def titleField = attrs.titleField ? attrs.titleField : "title"
        def bodyField = attrs.bodyField ? attrs.bodyField : "body"
				
		
        for (result in searchResults.resultList) {

            out << "<div class='hit'>"
            // build a URI to the original document, we always store class and
            // object id with the index for just this reason
            //			def hitUrl = request.contextPath + "/" + result.document.get("class").toLowerCase() +
            //				+ "/show/" + result.document.get("id")
            def hitUrl = request.contextPath + "/entries/jump/" + result.document.get("id")
			
            out << "<p class='hitTitle'>"
            out << "<a href='${hitUrl}'>"
            out << result.highlight[titleField]
            out << "</a>"
            out << "</p>"
            out << "<p class='hitInfo'>"
            out << result.document.get("blogTitle")
            out << " / "
			
            long blogDate = Long.parseLong(result.document.get("dateAdded"))
			
            out << new Date(blogDate)
            out << "</p>"
            out << "<p class='hitBody'>" + result.highlight[bodyField] + "</p>"
            out << "</div>"
        }
		
	
    }
	
    def searchCrumbs = { attrs ->
	
        def searchResults = attrs.results
        int hitsPerPage = searchResults.maxHitsRequested
        int totalHits = searchResults.totalHitCount
        int currentOffset = searchResults.totalHitsOffset
        def fields = searchResults.fields.join(",")
        def query = searchResults.queryTerms
		
        int totalPages = totalHits / hitsPerPage
		
        if (totalPages > 0) {
            out << "<ul class='searchCrumbs'>"
            for (p in 0..totalPages) {
				
                def offset = p * hitsPerPage
				
                def searchUrl = request.requestURI + "?query=${query}&fields=${fields}&offset=${offset}&hitcount=${hitsPerPage}"
				
                out << "<li> "
				
                def liBody
                def liClass
				
                if (offset == currentOffset) {
                    liBody = "${p+1}"
                    liClass ="currentPage"
                } else {
                    liBody = "<a href='$searchUrl'>${p+1}</a>"
                    if (offset < currentOffset) {
                        liClass = "prevPage"
                    } else {
                        liClass = "nextPage"
                    }
                }
				
                out << "<li class='${liClass}'>"
                out << liBody
                out << "</li>"
            }
            out << "</ul>"
        }
	
    }

}