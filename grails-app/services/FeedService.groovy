import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.methods.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class FeedService {
	
	//def log = LogFactory.getLog(this.class.name)
	
	SearchService searchService
	CacheService cacheService
	ThumbnailService thumbnailService
	
	boolean transactional = true
	
	def getFeedInfo(feedUrlStr) {
	
		def feedStr = getHtmlForUrl(feedUrlStr)
		
		def sfi = new com.sun.syndication.io.SyndFeedInput()
		// def feedReader = new StringReader(feedStr)
		
		// need this to handle UTF8 encoding correctly
		def bais = new ByteArrayInputStream(feedStr.getBytes("UTF-8"))
		def feedReader = new com.sun.syndication.io.XmlReader(bais)

		def sf = sfi.build(feedReader)

		
		def feedInfo = new FeedInfo(title: sf.title, 
				description: sf.description ? sf.description : "", 
				author: sf.author, type: sf.feedType)
		
		for (e in sf.entries) {
			String title = e.title
			String description = e.description?.value
			if (!description) {  // mustn't be rss... could be atom
				description = e.contents[0]?.value
			}
			// trim to 4k-ish size for db storage
			if (description.length() > 4000) {
				description = description[0..3999]
			}
			String link = e.link
			Date publishedDate = e.publishedDate
			def summary
			if (description) {
				// strip html for the summary, then truncate 
				summary = description.replaceAll("</?[^>]+>", "")
				summary = summary.length() > 200 ? summary[0..199] : summary 
			}
			def feedEntry = new FeedEntry(title: title, link: link, publishedDate: publishedDate,
					description: description ? description : "", 
					summary: summary ? summary : "",
					author: e.author ? e.author : "")
			log.debug("Found entry with title [$title] and summary [$summary] and link [$link]")
			feedInfo.entries.add(feedEntry)
		}
		
		
		// return "Author $sf.author Title $sf.title Desc $sf.description Feedtype $sf.feedType"
		return feedInfo
	
	}
	
	String getHtmlForUrl(url) { 
	
		log.info("Trying to fetch [$url]")

        def client = new HttpClient()
        def clientParams = client.getParams()
        clientParams.setParameter(org.apache.commons.httpclient.params.HttpClientParams.HTTP_CONTENT_CHARSET, "UTF-8");

        if (ConfigurationHolder.config.http.useproxy) {
			def hostConfig = client.getHostConfiguration()
			hostConfig.setProxy(ConfigurationHolder.config.http.host, ConfigurationHolder.config.http.port)
			log.warn("Setting proxy to [" + ConfigurationHolder.config.http.host + "]")
		}
		
		if (ConfigurationHolder.config.http.useragent) {
			clientParams.setParameter(org.apache.commons.httpclient.params.HttpClientParams.USER_AGENT,
					ConfigurationHolder.config.http.useragent)
		}

        if (ConfigurationHolder.config.http.timeout) {

		    clientParams.setParameter(org.apache.commons.httpclient.params.HttpClientParams.SO_TIMEOUT,
					ConfigurationHolder.config.http.timeout)
        }
		
        def mthd = new GetMethod(url)  
	        
        def statusCode = client.executeMethod(mthd)
        def responseBody = mthd.getResponseBody()
        mthd.releaseConnection()
        
        def urlStr = new String(responseBody)
		
		log.debug("Fetched [$url] successfully")
		
        return urlStr	
	    	
    }
	
	void updateFeed(blog) {
		
		log.info("Now polling: [$blog.title]")
		FeedInfo fi 
		try {
			fi = getFeedInfo(blog.feedUrl)
		} catch (Exception e) {
			log.warn("Could not parse feed [$blog.feedUrl]", e)		
			blog.status = "Error parsing [$blog.feedUrl] " + e.message
		}
		
		// we iterate in reverse to ensure newest entries have the newest timestamps
		fi?.entries?.reverseEach { entry ->
			
			log.debug("Looking for $entry.link")
			if (!BlogEntry.findByLink(entry.link)) {
				
				log.debug("Creating entry with title [$entry.title] and summary [$entry.summary] and link [$entry.link]")
				
				BlogEntry be = new BlogEntry(title: entry.title, link: entry.link, 
						description: entry.description, 
						summary: entry.summary, language: entry.language)
				
				log.debug("Saving entry with title [$be.title] and desc [$be.description]")
				
				if (be.isGroovyRelated()) {
					log.info("Added new entry: $be.title")
					if (!blog.validate()) {
						log.warn("Validation failed adding entry $be.title")
						blog.errors.allErrors.each {
					        log.warn(it)
					    }
					} else {
						log.debug("Saving entry: $be.title")
						blog.addToBlogEntries(be)
						blog.save()
						log.debug("Saved entry")
						// write out a thumbnail image if we need to...
                        // thumbnailService.getFile("${be.id}", true, true)

					}
					
					log.debug("Saved entry with title [$be.title] and desc [$be.description]")
		
					
					// and make it searchable
					searchService.index(be)
				} else {
					log.debug("Ignoring non-groovy blog entry: $be.title")
				}
			}
			
		}
		blog.lastPolled = new Date()
		long nextPollTime = new Date().getTime() + blog.pollFrequency * 60 * 60 * 1000
		blog.nextPoll = new Date(nextPollTime)
		if (!blog.validate()) {
			log.warn("Validation failed updating blog [$blog.title]")
			blog.errors.allErrors.each {
		        log.warn(it)
		    }
		} else {
			log.debug("Saved blog: " + blog.save())
		}
		log.debug("Next poll of [$blog.title] at $blog.nextPoll")
		
	}
	
	void updateFeeds() {
		
		log.info("FeedService starting polled update")
		def feedsToUpdate = Blog.findAllByNextPollLessThan(new Date())
		log.info("${feedsToUpdate.size()} to update")
		
		// Limit to 5 updated blogs per minute. Could probably up this significantly
		// by going multithreaded...
		if (feedsToUpdate.size() > ConfigurationHolder.config.http.maxpollsperminute) {
			log.warn("${feedsToUpdate.size()} exceeds max for this minute. Limiting update to ${ConfigurationHolder.config.http.maxpollsperminute}.")
			feedsToUpdate = feedsToUpdate[0..ConfigurationHolder.config.http.maxpollsperminute-1]
		}
		feedsToUpdate.each { blog ->
			updateFeed(blog)
		}
		log.info("FeedService finished polled update")
	}
	
	
	def updateLists() {
		
		def allEntries = []
		
		ConfigurationHolder.config.lists.each { name, url ->
		
			log.info("Updating list [$name] from [$url]")
			def feed = getFeedInfo(url)
			
			Blog listBlog = new Blog(title: feed.title)
			
			def filter = new Date().minus(1) // 1 days ago
			
			// Add 8 hours from Nabble feed time... 
			def rightDates = feed.entries.collect { entry -> 
					def diff = entry.publishedDate.time + 1000 * 60 * 60 * 7
					entry.publishedDate = new Date(diff)
					return entry
			}
			def feedEntries = rightDates.findAll { entry -> entry.publishedDate.after(filter) }
			log.info("Filtered original entries from " + feed.entries.size() + " to " + feedEntries.size())
			feedEntries.each { entry ->
			
				entry.info = name
				allEntries << entry
				
			}
		}
		
		// sort in date desc
		allEntries = allEntries.sort { e1, e2 ->
			
			if (e1.publishedDate == e2.publishedDate) { 
				return 0
			} else { 
				return e1.publishedDate > e2.publishedDate ? -1 : 1
			}
		}
		
		log.debug("Putting to cache: " + allEntries.size())
		
		cacheService.putToCache("listCache", 60 * 60, "listEntries", allEntries)
		
		return allEntries
			
	}
	
	def getCachedListEntries() {
		
		def listEntries = cacheService.getFromCache("listCache", 60 * 60, "listEntries")
		if (!listEntries) {
			listEntries = updateLists()
		}
		return listEntries
		
	}
	
}

