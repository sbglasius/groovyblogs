package org.groovyblogs

class Blog {

    String feedUrl
    String type
    String title
    String description
    Integer pollFrequency = 3
    Date lastPolled = new Date()
    Date nextPoll = new Date()
    Date registered = new Date()
    BlogStatus status = BlogStatus.PENDING
    Integer errorCount = 0
    String lastError

    static belongsTo = [account: User]
    static hasMany = [blogEntries: BlogEntry]

    static constraints = {
        feedUrl url: true, blank: false, unique: true, validator: { val, obj ->
            return !(val =~ /groovyblogs/)
        }
        type nullable: true
        pollFrequency inList: [1, 3, 12, 24]
        title nullable: true
        description nullable: true
        status nullable: true
        lastError nullable: true
    }

    static mapping = {
        nextPoll index: 'Next_Poll_Idx'
    }

    String toString() {
        "org.groovyblogs.Blog(#$id, $title, $feedUrl)"
    }
}
