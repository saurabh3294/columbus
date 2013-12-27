package com.proptiger.data.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Caching {
	@Cacheable(key ="#key", value ="#{cacheName==null? \"cache\" : cacheName}")
	public <T> T getSavedResponse(String key, Class<T> object, String cacheName){
		return null;
	}
	
	@CachePut(key="#key", value="#{cacheName==null? \"cache\" : cacheName}")
	public <T> T saveResponse(String key, T response, String cacheName){
		return response;
	}
	
	@CacheEvict(key="#key", value="#{cacheName==null? \"cache\" : cacheName}", beforeInvocation=true)
	public void deleteResponseFromCache(String key, String cacheName){
		
	}
	
	@Cacheable(key="#key", value="#{cacheName==null? \"cache\" : cacheName}")
	public <T> T getCachedSavedResponse(String key, T data, String cacheName){
		return data;
	}
}
