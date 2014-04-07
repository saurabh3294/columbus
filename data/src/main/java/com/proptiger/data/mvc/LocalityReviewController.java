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

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewRatingDetails;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.util.Constants;

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
    public ProAPIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId, @RequestParam(
            required = false) Integer numberOfReviews) {

        if (localityId == null || localityId < 1)
            return new ProAPIErrorResponse("Error", "Enter Valid Locality Id");

        LocalityReviewRatingDetails reviewRatingDetails = localityReviewService.getLocalityReviewRatingDetails(
                localityId,
                numberOfReviews);
        return new ProAPISuccessResponse(reviewRatingDetails);
    }

    @RequestMapping(value = "data/v1/entity/user/locality/{localityId}/review", method = RequestMethod.POST)
    @ResponseBody
    public ProAPIResponse createReview(
            @PathVariable Integer localityId,
            @RequestBody LocalityReviewComments reviewComments,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        LocalityReviewComments created = localityReviewService.createLocalityReviewComment(
                localityId,
                reviewComments,
                userInfo.getUserIdentifier());
        return new ProAPISuccessResponse(created);
    }
    
    
    @RequestMapping(value = "data/v1/entity/locality/review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getReview(@ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<LocalityReviewComments>> paginatedResponse = localityReviewService.getLocalityReview(
                null,
                selector);
        return new ProAPISuccessCountResponse(
                super.filterFieldsFromSelector(paginatedResponse.getResults(), selector),
                paginatedResponse.getTotalCount());
    }
    
    @RequestMapping(value = "data/v1/entity/user/locality/review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getReviewForUser(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<LocalityReviewComments>> paginatedResponse = localityReviewService.getLocalityReview(
                userInfo.getUserIdentifier(),
                selector);
        return new ProAPISuccessCountResponse(
                super.filterFieldsFromSelector(paginatedResponse.getResults(), selector),
                paginatedResponse.getTotalCount());
    }
}
