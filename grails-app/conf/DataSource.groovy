
dataSource {
    pooled = false // JNDI, baby. All the way...
}

/*
// Old School version for fairies...
driverClassName = "org.postgresql.Driver"
url = "jdbc:postgresql://localhost/groovyblogs"
username = "glen"
password = "password"
dbCreate = "update"


 */


hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='org.hibernate.cache.EhCacheProvider'
}

// environment specific settings

environments {
    development {
        dataSource {
            // check out /web-app/WEB-INF/jetty-env.xml for the details
            //dbCreate = "update"
            //jndiName = "java:comp/env/jdbc/groovyblogs"
            dbCreate = "update"
            pooled = false
            driverClassName = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost/groovyblogs"
            username = "glen"
            password = "password"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:hsqldb:mem:testDb"
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            jndiName = "java:comp/env/jdbc/groovyblogs"
        }
    }
}

