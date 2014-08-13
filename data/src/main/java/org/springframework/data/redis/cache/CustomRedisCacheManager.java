package org.springframework.data.redis.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.util.Constants;

/**
 * Redis cache manager that is central point to create redis cache instance,
 * this is just a wrapper over RedisCacheManager to create cache name
 * dynamically based on X-app-name header.
 * 
 * Kept this class in same package as of RedisCacheManager to access RedisCache
 * class.
 * 
 * @author Rajeev Pandey
 *
 */
public class CustomRedisCacheManager implements CacheManager {

    // fast lookup by name map
    private final ConcurrentMap<String, Cache> caches            = new ConcurrentHashMap<String, Cache>();
    private final Collection<String>           names             = Collections.unmodifiableSet(caches.keySet());
    private final RedisTemplate                template;

    private boolean                            usePrefix;
    private RedisCachePrefix                   cachePrefix       = new DefaultRedisCachePrefix();

    // 0 - never expire
    private long                               defaultExpiration = 0;
    private Map<String, Long>                  expires           = null;

    public CustomRedisCacheManager(RedisTemplate template) {
        this.template = template;
    }

    public Cache getCache(String name) {
        name = decideCacheName(name);
        Cache c = caches.get(name);
        if (c == null) {
            long expiration = computeExpiration(name);
            c = new RedisCache(name, (usePrefix ? cachePrefix.prefix(name) : null), template, expiration);
            caches.put(name, c);
        }

        return c;
    }

    public Cache getCache(String name, long expiration) {
        name = decideCacheName(name);
        Cache c = caches.get(name);
        if (c == null) {
            c = new RedisCache(name, (usePrefix ? cachePrefix.prefix(name) : null), template, expiration);
            caches.put(name, c);
        }

        return c;
    } 
    /**
     * This method modify the cache name based on application name header. So at
     * runtime if user request with this header then different cache should be
     * used to get and put data from/to redis cache.
     * 
     * @param name
     * @return
     */
    private String decideCacheName(String name) {
        RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();
        if(requestAttribute != null){
            HttpServletRequest request = ((ServletRequestAttributes) requestAttribute).getRequest();
            String appname = request.getHeader(Constants.APPLICATION_NAME_HEADER);
            if (appname != null && !appname.isEmpty()) {
                return appname + "-" + name;
            }
        }
       
        return name;
    }

    private long computeExpiration(String name) {
        Long expiration = null;
        if (expires != null) {
            expiration = expires.get(name);
        }
        return (expiration != null ? expiration.longValue() : defaultExpiration);
    }

    public Collection<String> getCacheNames() {
        return names;
    }

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    /**
     * Sets the cachePrefix.
     *
     * @param cachePrefix
     *            the cachePrefix to set
     */
    public void setCachePrefix(RedisCachePrefix cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    /**
     * Sets the default expire time (in seconds).
     *
     * @param defaultExpireTime
     *            time in seconds.
     */
    public void setDefaultExpiration(long defaultExpireTime) {
        this.defaultExpiration = defaultExpireTime;
    }

    /**
     * Sets the expire time (in seconds) for cache regions (by key).
     *
     * @param expires
     *            time in seconds
     */
    public void setExpires(Map<String, Long> expires) {
        this.expires = (expires != null ? new ConcurrentHashMap<String, Long>(expires) : null);
    }
}
