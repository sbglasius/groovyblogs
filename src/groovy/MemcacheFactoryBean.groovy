import org.springframework.beans.factory.config.AbstractFactoryBean
import net.spy.memcached.*
/**
 * A simple factory bean for Memcache Clients.
 * 
 * @author Glen Smith
 */
public class MemcacheFactoryBean extends AbstractFactoryBean {
    
    String host
    int port

    public Object createInstance() {
        return new MemcachedClient(
             new InetSocketAddress(host, port))

    }

    public Class getObjectType() {
        return MemcachedClient.class
    }


	
}

