package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.WordpressPost;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.BlogNewsService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "v1/entity/blognews")
public class BlogNewsController extends BaseController{

	@Autowired
	private BlogNewsService blogNewsService;
	
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getBlogNewsForCity(@RequestParam(required = true, value="cityName") String cityName){
		List<WordpressPost> newsList = blogNewsService.getBlogNewsPostsByCity(cityName);
		return new ProAPISuccessResponse(newsList);
	}
}
