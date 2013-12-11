package com.proptiger.data.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Caching {
	@Cacheable(key ="#key", value ="cache")
	public <T> T getSavedResponse(String key, Class<T> object){
		return null;
	}
	
	@CachePut(key="#key", value="cache")
	public <T> T saveResponse(String key, T response){
		return response;
	}
	
	@CacheEvict(value="cache", key="#key", beforeInvocation=true)
	public void deleteResponseFromCache(String key){
		
	}
}