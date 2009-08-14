import org.springframework.beans.factory.config.AbstractFactoryBean
import net.spy.memcached.MemcachedClient
import net.sf.ehcache.Element

/**
 * A Caching map backed by Memcache
 *
 * @author Glen Smith
 */
class MemcacheMap {

    MemcachedClient client
    int timeToLive

    public void put(Element e) {
        client.set(e.key, timeToLive, e.value)
    }

    public Object get(String key) {
        def value = client.get(key)
        if (value) {
            value = new Element(key, value)
        }
        return value
    
    }
	
}

