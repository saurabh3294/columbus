package com.proptiger.data.mvc.portfolio;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.portfolio.ForumUserSavedSearch;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.portfolio.ForumUserSavedSearchesService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio/saved-searches")
public class ForumUserSavedSearchesController extends BaseController {

	@Autowired
	private ForumUserSavedSearchesService savedSearchesService;
	
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getSavedSearches(@PathVariable Integer userId, @RequestParam(required = false, value = "selector") String selectorStr){
		Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
		List<ForumUserSavedSearch> result = savedSearchesService.getUserSavedSearches(selector, userId);
		
		Set<String> fieldsToSerialize = null;
		if(selector != null){
			fieldsToSerialize = selector.getFields();
		}
		return new ProAPISuccessResponse(super.filterOutAllExcept(result, fieldsToSerialize), result.size());
	}
}
