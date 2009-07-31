import org.springframework.cache.ehcache.EhCacheFactoryBean

beans = {

    feedCache(EhCacheFactoryBean) {
        timeToLive = 60
    }

    thumbCache(EhCacheFactoryBean) {
        eternal = true
		overflowToDisk = false
		maxElementsInMemory = 1000
    }

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