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

    @RequestMapping(value = "data/v1/entity/locality/{localityId}/review", method = RequestMethod.GET)
    @ResponseBody
    @DisableCaching
    public ProAPIResponse getLocalityReviews(@PathVariable Integer localityId, @RequestParam(
            required = false,
            value = "selector") String selectorStr) {
        Selector selector = new Selector();
        if (selectorStr != null) {
            selector = super.parseJsonToObject(selectorStr, Selector.class);
        }
        List<LocalityReviewComments> reviews = localityReviewService.getLocalityReview(localityId, null, selector);
        return new ProAPISuccessCountResponse(super.filterFields(reviews, selector.getFields()), reviews.size());
    }

    @RequestMapping(value = "data/v1/entity/user/locality/{localityId}/review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityReviewsByUser(@PathVariable Integer localityId, @RequestParam(
            required = false,
            value = "selector") String selectorStr, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Selector selector = new Selector();
        if (selectorStr != null) {
            selector = super.parseJsonToObject(selectorStr, Selector.class);
        }
        List<LocalityReviewComments> reviews = localityReviewService.getLocalityReview(
                localityId,
                userInfo.getUserIdentifier(),
                selector);
        return new ProAPISuccessCountResponse(super.filterFields(reviews, selector.getFields()), reviews.size());
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
    
    /**
     * This method will get review order, by overallRating
     * @param localityId
     * @param selectorStr
     * @return
     */
    @RequestMapping(value = "data/v1/entity/locality/{localityId}/top-rated-review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityReviewsOrderByRating(@PathVariable Integer localityId, @RequestParam(
            required = false,
            value = "selector") String selectorStr) {
        Selector selector = new Selector();
        if (selectorStr != null) {
            selector = super.parseJsonToObject(selectorStr, Selector.class);
        }
        PaginatedResponse<List<LocalityReviewComments>> reviews = localityReviewService.getLocalityReviewOrderByRating(
                localityId,
                null,
                selector);
        return new ProAPISuccessCountResponse(
                super.filterFields(reviews.getResults(), selector.getFields()),
                reviews.getTotalCount());
    }

    /**
     * This method will get review, order by overallRating
     * @param localityId
     * @param selectorStr
     * @param userInfo
     * @return
     */
    @RequestMapping(value = "data/v1/entity/user/locality/{localityId}/top-rated-review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityReviewsByUserOrderByRating(@PathVariable Integer localityId, @RequestParam(
            required = false,
            value = "selector") String selectorStr, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Selector selector = new Selector();
        if (selectorStr != null) {
            selector = super.parseJsonToObject(selectorStr, Selector.class);
        }
        PaginatedResponse<List<LocalityReviewComments>> reviews = localityReviewService.getLocalityReviewOrderByRating(
                localityId,
                userInfo.getUserIdentifier(),
                selector);
        return new ProAPISuccessCountResponse(
                super.filterFields(reviews.getResults(), selector.getFields()),
                reviews.getTotalCount());
    }
    
    @RequestMapping(value = "data/v1/entity/city/{cityId}/locality-review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityReviewsOfCity(@PathVariable Integer cityId, @ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<LocalityReviewComments>> reviewsOfCity = localityReviewService.getLocalityReviewOfCity(
                cityId,
                selector);
        return new ProAPISuccessCountResponse(reviewsOfCity.getResults(), reviewsOfCity.getTotalCount());
    }
    
    @RequestMapping(value = "data/v1/entity/suburb/{suburbId}/locality-review", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityReviewsOfSuburb(@PathVariable Integer suburbId, @ModelAttribute FIQLSelector selector) {
        PaginatedResponse<List<LocalityReviewComments>> reviewsOfCity = localityReviewService.getLocalityReviewOfSuburb(
                suburbId,
                selector);
        return new ProAPISuccessCountResponse(reviewsOfCity.getResults(), reviewsOfCity.getTotalCount());
    }
}
