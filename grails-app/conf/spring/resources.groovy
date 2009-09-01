import org.springframework.cache.ehcache.EhCacheFactoryBean

beans = {

    memCache(MemcacheFactoryBean) {
        host = "localhost"
        port = 11211
    }

    thumbCache(MemcacheMap) {
        client = memCache
        timeToLive = 60*60*24*14 // 14 days
    }

    feedCache(EhCacheFactoryBean) {
        timeToLive = 60
    }

    /*
    thumbCache(EhCacheFactoryBean) {
        eternal = true
		overflowToDisk = false
		maxElementsInMemory = 1000
    }
    */

    pendingCache(EhCacheFactoryBean) {
        timeToLive = 90
    }

    listCache(EhCacheFactoryBean) {
        timeToLive = 900
    }

    tweetCache(EhCacheFactoryBean) {
        timeToLive = 900
    }

    chartCache(EhCacheFactoryBean) {
        timeToLive = 3600
    }

    entriesCache(EhCacheFactoryBean) {
        timeToLive = 60
    }

    recentBlogsCache(EhCacheFactoryBean) {
        timeToLive = 900
    }

    recentStatsCache(EhCacheFactoryBean) {
        timeToLive = 3600
    }

  

}