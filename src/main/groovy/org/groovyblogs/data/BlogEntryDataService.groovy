package org.groovyblogs.data

import grails.gorm.services.Service
import grails.gorm.services.Where
import org.groovyblogs.BlogEntry

@Service(BlogEntry)
interface BlogEntryDataService {

    List<BlogEntry> list(Map args)

    @Where({ dateAdded >= date })
    List<BlogEntry> listAfterDate(Map args, Date date)

    @Where({ dateAdded >= date && hitCount > 0 })
    List<BlogEntry> listAfterDateWithHitCount(Map args, Date date)

}
