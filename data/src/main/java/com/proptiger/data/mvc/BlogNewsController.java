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
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.BlogNewsService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/blog-news")
public class BlogNewsController extends BaseController {

	@Autowired
	private BlogNewsService blogNewsService;

	@RequestMapping
	@ResponseBody
	public ProAPIResponse getBlogNewsForCity(
			@RequestParam(required = true, value = "cityName") String cityName,
			@RequestParam(required = false, defaultValue = "200", value = "contentLimit") int contentLimit,
			@RequestParam(required = false, value = "selector") String selector) {

		Selector blogSelector = super.parseJsonToObject(selector, Selector.class);
        if (blogSelector == null) {
        	blogSelector = new Selector();
        }
		List<WordpressPost> newsList = blogNewsService
				.getBlogNewsPostsByCity(cityName, contentLimit, blogSelector);
		return new ProAPISuccessResponse(newsList);
	}
}
