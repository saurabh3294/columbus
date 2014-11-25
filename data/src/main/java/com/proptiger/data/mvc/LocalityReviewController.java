package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewRatingDetails;
import com.proptiger.data.service.LocalityReviewService;

/**
 * Controller for get/create/update/delete API related to locality review
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
public class LocalityReviewController extends BaseController {

    @Autowired
    private LocalityReviewService localityReviewService;

    @RequestMapping(value = "data/v1/entity/locality-review", method = RequestMethod.GET)
    @ResponseBody
    @DisableCaching
    @Deprecated
    public APIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId, @RequestParam(
            required = false) Integer numberOfReviews) {

        LocalityReviewRatingDetails reviewRatingDetails = localityReviewService.getLocalityReviewRatingDetails(
                localityId,
                numberOfReviews);
        return new APIResponse(reviewRatingDetails);
    }

    @RequestMapping(value = "data/v1/entity/user/locality/{localityId}/review", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse createReview(
            @PathVariable Integer localityId,
            @RequestBody LocalityReviewComments reviewComments,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        LocalityReviewComments created = localityReviewService.createLocalityReviewComment(
                localityId,
                reviewComments,
                userInfo.getUserIdentifier());
        return new APIResponse(created);
    }
    
    
    @RequestMapping(value = "data/v1/entity/locality/review", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getReview(@ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<LocalityReviewComments>> paginatedResponse = localityReviewService.getLocalityReview(
                null,
                selector);
        return new APIResponse(
                super.filterFieldsFromSelector(paginatedResponse.getResults(), selector),
                paginatedResponse.getTotalCount());
    }
    
    @RequestMapping(value = "data/v1/entity/user/locality/review", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getReviewForUser(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<LocalityReviewComments>> paginatedResponse = localityReviewService.getLocalityReview(
                userInfo.getUserIdentifier(),
                selector);
        return new APIResponse(
                super.filterFieldsFromSelector(paginatedResponse.getResults(), selector),
                paginatedResponse.getTotalCount());
    }
}
