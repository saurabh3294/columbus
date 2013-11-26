package com.proptiger.data.util;

import java.io.IOException;

import javax.annotation.PostConstruct;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.api.filter.DataAPIAuthenticationFilter;

/**
 * Util class to connect memcache and get the values from memcache
 * @author Rajeev Pandey
 *
 */
@Component
public class CacheClientUtil {
 
	private static MemcachedClient memcachedClient;
	private static final String MEMCACHE_URL_PORT = "memcache.url.port";
	@Autowired
	private PropertyReader propertyReader;
	private static final Logger logger = LoggerFactory.getLogger(DataAPIAuthenticationFilter.class);
	
	@PostConstruct
	private void init() throws IOException{
		try {
			memcachedClient = new MemcachedClient(
					AddrUtil.getAddresses(propertyReader
							.getRequiredProperty(MEMCACHE_URL_PORT)));
		} catch (IOException e) {
			logger.error("Exception while connecting to memcache", e);
			throw e;
		}
	}
	
	public static String getValue(String key){
		if(key == null || "".equals(key)){
			return null;
		}
		return (String)memcachedClient.get(key);
	}
	
}
