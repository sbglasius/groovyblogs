package org.groovyblogs

import org.slf4j.Logger
//import org.apache.lucene.search.HitCollector

/**
 * Provides a simple Collector that allows you to extract an offset
 * into the search results. Useful for implementing Paging of search
 * results in a web app.
 *
 * @author Glen Smith (glen@bytecode.com.au)
 *
 * TODO: Replace with elasticsearch
 */
class OffsetCollector { //} extends HitCollector {

    Logger log = Logger.getLogger(getClass().name)

    def topN = new TreeMap(new DescendingComparator())   // maps score to docid in desc order
    int hitCount
    int maxHits
    int offset

    /** Constructor. */
    OffsetCollector(int maxHits, int offset) {
        this.maxHits = maxHits
        this.offset = offset
    }

    /** Finds a unique key in the descending map to store this hit in.
     We need this to ensure that clashing keys don't clobber existing hits. */
    private void placeInMap(float score, int doc) {

        def availableKey = score
        // ensures key is unique in map
        while (topN.containsKey(availableKey)) {
            availableKey -= 0.00000001
        }
        topN[availableKey] = doc
    }

    /** The HitCollector interface method. Called by Lucene on each raw hit. */
    void collect(int doc, float score) {
        log.debug("Doc $doc is score $score")
        if (topN.size() < maxHits + offset) {
            placeInMap(score, doc)
        } else {
            // TODO docs with same hit score will currently get blatted
            float lowestSoFar = topN.lastKey()
            if (score > lowestSoFar) {
                topN.remove(lowestSoFar)
                topN[score] = doc
            }
        }
        hitCount++
    }

    /** Returns an array of DocIds that match the term. */
    def hits() {

        int i = 0
        def orderedValues = []
        if (topN.size() <= maxHits) {
            orderedValues = topN.values()
        } else {
            // skip our offset and then read the hits (we could be on page 3)
            topN.each { key, value ->
                if (i >= offset && i < (offset + maxHits)) {
                    orderedValues << value
                }
                i++
            }
        }
        log.debug("Returning ${orderedValues}")
        return orderedValues
    }
}

/**
 * Sorts a TreeMap in descending order.
 */
class DescendingComparator implements Comparator {
	int compare(num1, num2) { num2 <=> num1 }
}
