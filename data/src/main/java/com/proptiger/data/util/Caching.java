package com.proptiger.data.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Caching {
	@Cacheable(key ="#key", value ="cache")
	public Object getSavedResponse(String key){
		return null;
	}
	
	@CachePut(key="#key", value="cache")
	public Object saveResponse(String key, Object response){
		return response;
	}
	
	@CacheEvict(value="cache", key="#key", beforeInvocation=true)
	public void deleteResponseFromCache(String key){
		
	}
}
