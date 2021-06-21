import org.hibernate.dialect.MySQL5InnoDBDialect

dataSource {
    pooled = true
    jmxExport = true
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

environments {
    development {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://localhost:3306/groovyblogs_dev"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = MySQL5InnoDBDialect
            username = "root"
            password = "root"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
}
