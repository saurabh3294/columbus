package com.proptiger.data.mvc.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.data.model.user.SavedSearch;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.SavedSearchService;
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

    @RequestMapping(value = { "/portfolio/saved-searches", "/saved-searches" }, method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getSavedSearches(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<SavedSearch> result = savedSearchesService.getUserSavedSearches(userInfo.getUserIdentifier());

        return new APIResponse(result, result.size());
    }

    @RequestMapping(value = "/saved-searches", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse saveSearch(
            @RequestBody SavedSearch saveSearch,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        return new APIResponse(savedSearchesService.setUserSearch(saveSearch, userInfo.getUserIdentifier()));
    }

    @RequestMapping(value = "/saved-searches/{savedSearchId}", method = RequestMethod.DELETE)
    @ResponseBody
    public APIResponse deleteSavedSearch(
            @PathVariable int savedSearchId,
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        savedSearchesService.deleteSavedSearch(savedSearchId);
        return getSavedSearches(userId, userInfo);
    }
}
