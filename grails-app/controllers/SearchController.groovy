class SearchController {

	SearchService searchService
	
	def index = {
			redirect(action:search,params:params)
	}

	def indexAll = { 
		searchService.indexAll(Message.list());
	}
	
	def search = {
			def fields = params.fields
			def query = params.query
			
			int hitcount = params.hitcount ? Integer.parseInt(params.hitcount) : 10
			int offset = params.offset ? Integer.parseInt(params.offset) : 0
			
			if (fields && query) {
				
				log.debug("Field $fields with query $query with hitcount $hitcount and offset $offset")
				
				def fieldsList = fields.split(',')
				
				def results = searchService.search(query, fieldsList, hitcount, offset)
				 
				log.debug("Total query results [" + results.totalHitCount + "]")
				
				return [ results: results, query: query, fields: fields ]
				
			} else {
				
				return [ : ]
				
			}
			
	}
}

