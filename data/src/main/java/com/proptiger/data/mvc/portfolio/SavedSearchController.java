package com.proptiger.data.mvc.portfolio;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.portfolio.SavedSearch;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.portfolio.SavedSearchService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}")
public class SavedSearchController extends BaseController {
	@Autowired
	private SavedSearchService savedSearchesService;
	
	@RequestMapping(value = {"/portfolio/saved-searches", "/saved-searches"}, method=RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getSavedSearches(
			@PathVariable Integer userId,
			@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
		List<SavedSearch> result = savedSearchesService
				.getUserSavedSearches(userInfo.getUserIdentifier());

		return new ProAPISuccessCountResponse(result, result.size());
	}

	@RequestMapping(value="/saved-searches", method=RequestMethod.POST)
	@ResponseBody
	public ProAPIResponse saveSearch(@RequestBody SavedSearch saveSearch, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo ){
		return new ProAPISuccessResponse(savedSearchesService.setUserSearch(saveSearch, userInfo.getUserIdentifier()));
	}
	
    @RequestMapping(value = "/saved-searches/{savedSearchId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ProAPIResponse deleteSavedSearch(@PathVariable int savedSearchId, @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo)
    {
        savedSearchesService.deleteSavedSearch(savedSearchId);
        return getSavedSearches(userId, userInfo);
    }	
}
