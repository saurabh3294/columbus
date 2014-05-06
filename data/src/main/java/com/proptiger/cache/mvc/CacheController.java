package com.proptiger.cache.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.util.Caching;

@Controller
@RequestMapping(value = "cache")
@DisableCaching
public class CacheController {

	@Autowired
	private Caching caching;

	@RequestMapping(value = "/clear-cache")
	@ResponseBody
	public ProAPIResponse clearCache(
			@RequestParam(defaultValue = "") String cacheName) {
		StringBuilder cacheCleared = new StringBuilder();
		if(cacheName.isEmpty()){
			cacheCleared.append("All");
		}
		String[] caches = cacheName.split(",");
		for (String cache : caches) {
			caching.deleteMultipleResponseFromCacheOnRegex("*", cache);
			cacheCleared.append(cache).append(",");
		}
		return new ProAPISuccessResponse("Cleared Cache Names: "
				+ cacheCleared.toString());
	}

}
