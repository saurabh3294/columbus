package com.proptiger.data.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Caching {
	@Cacheable(key ="#key", value =Constants.CacheName.CACHE)
	public <T> T getSavedResponse(String key, Class<T> object){
		return null;
	}
	
	@CachePut(key="#key", value=Constants.CacheName.CACHE)
	public <T> T saveResponse(String key, T response){
		return response;
	}
	
	@CacheEvict(key="#key", value=Constants.CacheName.CACHE, beforeInvocation=true)
	public void deleteResponseFromCache(String key){
		
	}
	
	@Cacheable(key="#key", value=Constants.CacheName.CACHE)
	public <T> T getCachedSavedResponse(String key, T data){
		return data;
	}
}
