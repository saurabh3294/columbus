package com.proptiger.data.util;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class Caching {
    @Autowired
    private ApplicationContext applicationContext;
    
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
    
    @Deprecated
    public void deleteMultipleResponseFromCache(String keys[]){
        Caching cachingObject = applicationContext.getBean(Caching.class);
        
        for(int i=0; i<keys.length; i++)
            cachingObject.deleteResponseFromCache(keys[i]);
    }
}
