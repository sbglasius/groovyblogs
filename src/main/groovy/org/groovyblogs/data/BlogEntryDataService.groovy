package org.groovyblogs.data

import grails.gorm.services.Service
import grails.gorm.services.Where
import org.groovyblogs.BlogEntry

@Service(BlogEntry)
interface BlogEntryDataService {
    @Where ({ disabled == false })
    List<BlogEntry> list(Map params)

    @Where ( { disabled == false && dateAdded >= date})
    List<BlogEntry> listAfterDate(Map params, Date date)

    @Where ( { disabled == false && dateAdded >= date && hitCount > 0})
    List<BlogEntry> listAfterDateWithHitCount(Map params, Date date)

}
