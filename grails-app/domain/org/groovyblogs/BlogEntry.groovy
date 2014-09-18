package org.groovyblogs

class BlogEntry {
    static searchable = {
        only = ['title', 'description']
    }

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
        description(nullable: false)
        language(nullable: true)
        link(unique: true)
        hash(nullable: true)
    }


    def isGroovyRelated() {

        def keywords = Tag.findAllByApproved(true)*.tag

        return keywords.any { keyword ->
            keyword = /\b${keyword}\b/
            return title?.toLowerCase() =~ keyword || description?.toLowerCase() =~ keyword
        }
    }

    String toThumbnailPath() {
        return dateAdded.format('yyyy/MM/dd') + "/"

    }

    static mapping = {
        description sqlType: 'LONGTEXT'
        link index: 'Link_Idx'
        hash index: 'Hash_Idx'
    }

}	
