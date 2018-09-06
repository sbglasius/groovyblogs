package org.groovyblogs

class SearchResults {

    List<SearchResult> resultList

    // total number of hits returned from Lucene
    int totalHitCount

    // number of hits we returned to the user (could be paging through them)
    int returnedHitCount

    // max hits requested by the user
    int maxHitsRequested

    // offset into total hits for starting our list of documents
    int totalHitsOffset

    // original query terms
    def queryTerms

    // original fields to search
    def fields

    // time (in ms) the query took to execute
    int queryTime

    // total number of documents in index
    int totalDocsInIndex

}