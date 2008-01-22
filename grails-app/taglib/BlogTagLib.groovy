import org.codehaus.groovy.grails.commons.ConfigurationHolder

class BlogTagLib {
	
	CacheService cacheService
	
	def summariseEntry = { attrs ->
			
		def description = attrs.description
		
		// strip html for the summary, then truncate 
		def summary = FeedEntry.summarize(description)
	    out << summary
			
	}
	
	
	public static String getNiceDate(Date date) {
		
		def now = new Date()
		
		def diff = Math.abs(now.getTime() - date.getTime())
		
		long second = 1000
		long minute = 1000 * 60
		long hour = minute * 60
		long day = hour * 24
		
		def niceTime = ""
		
		long calc = 0L;

		calc = Math.floor(diff / day)
		if (calc > 0) {
			niceTime += calc + " day" + (calc > 1 ? "s " : " ")
			diff = diff % day
		}
		
		calc = Math.floor(diff / hour)
		if (calc > 0) {
			niceTime += calc + " hour" + (calc > 1 ? "s " : " ")
			diff = diff % hour
		}
		
		calc = Math.floor(diff / minute)
		if (calc > 0) {
			niceTime += calc + " minute" + (calc > 1 ? "s " : " ")
			diff = diff % minute
		}
		
		if (niceTime.length() == 0) {
			niceTime = "Right now"	
		} else {
			niceTime += (date.getTime() > now.getTime()) ? "from now" : "ago"
		}

		return niceTime
		
	}
	
	
	def dateFromNow = { attrs ->
			
		def date = attrs.date
				
		out << getNiceDate(date)
			
	}
	
	def niceDate = { attrs ->
	
		def date = attrs.date
		def sdf = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm")
		out << sdf.format(date)
		
	}
	
	def translate = { attrs ->
		
		if (ConfigurationHolder.config.translate.enabled) {
	
			def entry = attrs.entry
	
			if (entry.language && entry.language != 'english') {
				def langCode = ConfigurationHolder.config.translate.langs.findAll { key, value ->
						entry.language == key
				}
				
				if (langCode[entry.language]) {
	    	        out << "<span class='translateLink'>"
	    	        out << "[ <a href='"
	    	        out << "jumpTranslate/$entry.id?lang=" + langCode[entry.language]
	    	        // createLink(controller: 'entries', action: 'jumpTranslate', id: entry.id, params: [ lang: langCode[entry.language] ] )
	    	        out << "'>Translate</a> ]"
	    	        out << "</span>"
				}
				
				
			}
			
		}

	
	}
	
	// Creates a list of recent bloggers
	def recentBloggers = { attrs ->
	
		def cachePeriod = 60 * 60 // one hour
		def cacheValue = cacheService.getFromCache ("blogs", cachePeriod, "recentBloggers")
		if (cacheValue) {
			
			out << cacheValue
			
		} else {
			 
			def maxEntries = attrs.max ? attrs.max : 5
			def recentBlogs = Blog.listOrderByRegistered(max:5, order:"desc")
			
			def sw = new StringWriter()
			def outs = new PrintWriter(sw)
			
			outs << "<ul id='recentBloggers'>"
			recentBlogs.each { blog -> 
			
				outs << "<li>"
				// createLink(controller: 'blog', action: 'show', id: blog.id)
				outs << "<a href='../blog/show/" + blog.id + "'>"
				outs << blog.title
				outs << "</a>"
					
				outs << "</li>"
					// <a href="<g:createLink controller='blog' action='show' id='blog.id'>">${blog.title}</a></li>  
			}
			outs << "</ul>"
			
			def recentStr = sw.toString()
			out << recentStr
			// println "List is: ${recentStr}"
			cacheService.putToCache ("blogs", cachePeriod, "recentBloggers", recentStr)
			
		}

	}
	
	def recentStats = { attrs ->
	
		def cachePeriod = 60 * 60 // one hour
		def cacheStats = cacheService.getFromCache ("blogs", cachePeriod, "recentStats")
		if (cacheStats) {
			out << cacheStats
		} else {
			def newStats = """
							<p>${Blog.count()} Blogs Aggregated</p>
							<p>${BlogEntry.count()} Entries Indexed</p>
							<p>${BlogEntry.findAllByDateAddedGreaterThan(new Date().minus(1)).size()} Entries Last 24 hours</p>
			"""
			out << newStats
			cacheService.putToCache ("blogs", cachePeriod, "recentStats", newStats)			
		}
	
	}
	
	def feedburner = { attr ->
	
		if (ConfigurationHolder.config.http.usefeedburner) {
			out << """
			<p style='margin-top: 5px'>
					<img src="${ConfigurationHolder.config.http.feedburner_stats_url}" height="26" width="88" style="border:0" alt="Feedburner Stats" />
			</p>
			"""
		}
	
	
	}


}