package com.proptiger.data.util;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.proptiger.core.util.Constants;

@Component
public class Caching {
    @Autowired
    private ApplicationContext            applicationContext;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Cacheable(key = "#key", value = Constants.CacheName.CACHE)
    public <T> T getSavedResponse(String key, Class<T> object) {
        return null;
    }

    @CachePut(key = "#key", value = Constants.CacheName.CACHE)
    public <T> T saveResponse(String key, T response) {
        return response;
    }

    @CacheEvict(key = "#key", value = Constants.CacheName.CACHE, beforeInvocation = true)
    public void deleteResponseFromCache(String key) {

    }

    @Cacheable(key = "#key", value = Constants.CacheName.CACHE)
    public <T> T getCachedSavedResponse(String key, T data) {
        return data;
    }

    /**
     * This method will get from the cache. If the response is null then reset
     * the cache for this key and return the output.
     * 
     * @param key
     * @param data
     * @return
     */
    public <T> T getNonNullCachedResponse(String key, T data) {
        Caching cachingObject = applicationContext.getBean(Caching.class);

        T output = cachingObject.getCachedSavedResponse(key, data);

        if (output == null) {
            cachingObject.deleteResponseFromCache(key);
        }

        return output;
    }

    /**
     * This method will take array of keys and delete the cache for those keys.
     * 
     * @param keys
     */
    @Deprecated
    public void deleteMultipleResponseFromCache(String keys[]) {
        Caching cachingObject = applicationContext.getBean(Caching.class);

        for (int i = 0; i < keys.length; i++)
            cachingObject.deleteResponseFromCache(keys[i]);
    }

    /**
     * This method will take cacheName and the regex of the keys. It will reset
     * the cache for all the keys found from regex.
     * 
     * @param keyPattern
     * @param cacheName
     */
    @Deprecated
    public void deleteMultipleResponseFromCacheOnRegex(String keyPattern, String cacheName) {
        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();

        keyPattern = "*" + cacheName + "*" + keyPattern + "*";
        Set<byte[]> keys = redisConnection.keys(keyPattern.getBytes());

        for (byte[] key : keys) {
            redisConnection.del(key);
        }
    }

}
