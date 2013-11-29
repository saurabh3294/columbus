package com.proptiger.cache.mvc;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;

@Controller
@RequestMapping(value = "cache")
public class CacheController {
	
	@RequestMapping(value="/clear-cache")
	@ResponseBody
	@CacheEvict(value="cache", allEntries=true)
	public ProAPIResponse clearCache(){
		return new ProAPISuccessResponse("Cache Cleared.");
	}

}
