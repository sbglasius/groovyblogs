package org.groovyblogs

import net.spy.memcached.MemcachedClient

import org.springframework.beans.factory.config.AbstractFactoryBean

/**
 * A simple factory bean for Memcache Clients.
 *
 * @author Glen Smith
 */
class MemcacheFactoryBean extends AbstractFactoryBean {

    String host
    int port

    def createInstance() {
        new MemcachedClient(new InetSocketAddress(host, port))
    }

    Class getObjectType() { MemcachedClient }
}
