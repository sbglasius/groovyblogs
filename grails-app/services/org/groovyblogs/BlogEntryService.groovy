package org.groovyblogs

import grails.gorm.services.Service

@Service(BlogEntry)
interface BlogEntryService {

    BlogEntry get(Serializable id)

    List<BlogEntry> list(Map args)

    Long count()

    void delete(Serializable id)

    BlogEntry save(BlogEntry blogEntry)

}