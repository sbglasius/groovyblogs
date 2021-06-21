package org.groovyblogs

import net.sf.ehcache.Element
import net.spy.memcached.MemcachedClient

/**
 * A Caching map backed by Memcache
 *
 * @author Glen Smith
 */
class MemcacheMap {

    MemcachedClient client
    int timeToLive

    void put(Element e) {
        client.set(e.key, timeToLive, e.value)
    }

    def get(String key) {
        def value = client.get(key)
        if (value) {
            return new Element(key, value)
        }
    }
}
