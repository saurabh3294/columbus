package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.portfolio.RecentlyViewedPropertyService;

@Controller
@RequestMapping(value = "data/v1/entity/{userId}/portfolio/recently-viewd")
public class RecentlyViewedPropertyController extends BaseController{

	@Autowired
	private RecentlyViewedPropertyService recentlyViewdPropertyService;
	
	public ProAPIResponse getRecentlyViewed(@PathVariable String userId, @RequestParam String selectorStr){
		Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
		
		return new ProAPISuccessResponse();
	}
	
}
