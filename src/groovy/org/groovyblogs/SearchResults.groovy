package org.groovyblogs

class SearchResults {

    // list of org.groovyblogs.SearchResult objects
    def resultList

    // total number of hits returned from Lucene
    def totalHitCount

    // number of hits we returned to the user (could be paging through them)
    def returnedHitCount

    // max hits requested by the user
    def maxHitsRequested

    // offset into total hits for starting our list of documents
    def totalHitsOffset

    // original query terms
    def queryTerms

    // original fields to search
    def fields

    // time (in ms) the query took to execute
    def queryTime

    // total number of documents in index
    def totalDocsInIndex

}