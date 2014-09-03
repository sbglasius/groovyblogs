package org.groovyblogs

class BlogEntry {
    // TODO: Use elastic search instead
//    static searchable = {
//        only = ['title', 'description']
//    }

    String title
    String description
    String link
    Date dateAuthored = new Date()
    Date dateAdded = new Date()
    int hitCount = 0
    String language
    String info
    String hash

    // info is used for sticking stuff like "Only showing 3 entries for this user"
    static def transients = ["info"]
    // static def optionals = [ "language" ]

    Blog blog

    static belongsTo = Blog

    static def constraints = {
        title(size: 0..255)
        description(size: 0..4096)
        language(nullable: true)
        link(unique: true)
        hash(nullable: true)
    }


    def isGroovyRelated() {

        def keywords = ['groovy', 'grails', 'griffon', 'gorm', 'gr8', 'gant', 'gradle', 'gpars', 'gsp',
                        'geb', 'spock', 'gaelyk']

        boolean isGroovy = false
        keywords.each { keyword ->
            keyword = /\b${keyword}\b/
            if (title?.toLowerCase() =~ keyword) {
                isGroovy = true
                return
            }



            if (description?.toLowerCase() =~ keyword) {
                isGroovy = true
                return
            }


        }
        return isGroovy
    }

    String toThumbnailPath() {

        def sdf = new java.text.SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(dateAdded) + "/"

    }

    static mapping = {
        description sqlType: 'LONGTEXT'
        link index: 'Link_Idx'
        hash index: 'Hash_Idx'
    }

}	
