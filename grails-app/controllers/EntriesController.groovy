class EntriesController {

    FeedService feedService
    EntriesService entriesService
    
    def entriesCache
	
    def index() { redirect(action:'recent',params:params) }

    def recent() {

        int days = EntriesService.DEFAULT_DAYS_TO_REPORT // default to 7 days
        if (params.id) {
            days = Integer.parseInt(params.id) // override for longer periods
            if (days > 60) {
                days = 60
            }
        }
			
        def entries = entriesService.getRecentEntries(days)
			
        return [
            entries: entries, // entriesService.limitEntries(entries),
            pageTitle : "Recent Entries (Last ${days} Days)",
            thumbnails: grailsApplication.config.thumbnail.enabled
        ]
    }

    def endless = {

        params.offset = 0
        params.max = 3
        def entries = entriesService.getEndlessEntries(params)
        [
            entries: entriesService.limitEntries(entries),
            thumbnails: grailsApplication.config.thumbnail.enabled
        ]
    }

    def endlessNext =  {

        params.max = 3
        def entries = entriesServices.getEndlessEntries(params)
        //TODO return fragment
        render(template: 'entry', model: [
                entries: entriesService.limitEntries(entries),
                thumbnails: grailsApplication.config.thumbnail.enabled ])
        
    }

	
    def popular() {

        def entries = entriesService.getPopularEntries()
			
        render(view: 'recent',
            model: [ entries: entries,
                pageTitle: 'Popular Entries (Last 7 Days)',
                thumbnails: grailsApplication.config.thumbnail.enabled]
        )
    }
	
    def lists() {
        def entries = feedService.getCachedListEntries()
        render(view: 'lists',
            model: ['entries': entries] )
    }

    def tweets() {
        def entries = feedService.getCachedTweetEntries()
        [ entries : entries ]
    }
	
    def jump() {
			
        BlogEntry be = BlogEntry.get(params.id)
        if (be) {
            //TODO should be transactional
            be.hitCount++
            be.save()
            response.sendRedirect(be.link)
        } else {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: recent)
        }
			
    }
	
    def jumpTranslate() {
			
        BlogEntry be = BlogEntry.get(params.id)
        def lang = params.lang
        if (be && lang) {
            //TODO should be transactional
            be.hitCount++
            be.save()
				
            def engine = new groovy.text.SimpleTemplateEngine()
            def template = engine.createTemplate(grailsApplication.config.translate.url)
            def binding = [
                from: lang,
                to: "en",
                url: be.link.encodeAsURL(),
            ]
            def jumpTranslateUrl = template.make(binding).toString()
				
            response.sendRedirect(jumpTranslateUrl)
        } else {
            flash.message = "Could not find link for blogEntry id $params.id"
            redirect(action: recent)
        }
			
    }
}

