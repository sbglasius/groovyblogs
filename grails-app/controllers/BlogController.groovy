            
class BlogController extends BaseController {
	
    def index = { redirect(action:show, params:params) }

    def show = {
    	def blog = Blog.get(params.id)
    	def sortedEntries = blog.blogEntries.sort { e1, e2 ->
			if (e1.dateAdded == e2.dateAdded) { 
				return 0
			} else { 
				return e1.dateAdded > e2.dateAdded? -1 : 1
			}
		}
    	// trim to 20 most recent entries
    	if (sortedEntries.size() > 20) {
    		sortedEntries = sortedEntries[0..19]
    	}
    	
        [ blog : blog, sortedEntries : sortedEntries ]
    }



}