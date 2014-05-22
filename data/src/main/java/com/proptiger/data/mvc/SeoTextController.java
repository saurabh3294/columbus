/**
 * 
 */
package com.proptiger.data.mvc;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.proptiger.data.model.URLDetail;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.SeoPageService;
import com.proptiger.data.service.URLService;

/**
 * @author mandeep
 * 
 */
@Controller
@RequestMapping("data/v1/seo-text")
public class SeoTextController {
	private RestTemplate restTemplate = new RestTemplate();

	@Value("${proptiger.url}")
	private String websiteHost;
	
	@Autowired
	private SeoPageService seoPageService;
	
	@Autowired
	private URLService urlService;
	/*
	@RequestMapping
	@ResponseBody
	public APIResponse get(@RequestParam String url) {
		return new APIResponse(new Gson().fromJson(
				restTemplate.getForObject(websiteHost
						+ "getSeoTags.php?url={URL}", String.class,
						Collections.singletonMap("URL", url)), Object.class));
	}
	*/
	@RequestMapping("/test")
	@ResponseBody
	public APIResponse getSeo(@RequestParam String url) throws FileNotFoundException, IllegalAccessException, InvocationTargetException {
		URLDetail urlDetail = new URLDetail();
		
		urlDetail = urlService.parse(url);
		return new APIResponse(seoPageService.choosePage(urlDetail));
				
	}
}
