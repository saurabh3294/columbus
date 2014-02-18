package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.LocalityRatings;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityRatingService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
public class LocalityRatingController extends BaseController {

    @Autowired
    private LocalityRatingService localityRatingService;

    /**
     * This method will create locality rating for non logged in or unregistered
     * user
     * 
     * @param localityId
     * @param localityReview
     * @return
     */
    @RequestMapping(value = { "data/v1/entity/locality/{localityId}/rating" }, method = RequestMethod.POST)
    @ResponseBody
    @DisableCaching
    public ProAPIResponse createLocalityRating(
            @PathVariable Integer localityId,
            @RequestBody LocalityRatings localityReview) {
        LocalityRatings createdRating = localityRatingService.createLocalityRating(null, localityId, localityReview);
        return new ProAPISuccessResponse(createdRating);
    }

    /**
     * Get locality rating for locality id done by user
     * 
     * @param localityId
     * @param userInfo
     * @return
     */
    @RequestMapping(value = { "data/v1/entity/user/locality/{localityId}/rating" }, method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityRatingByUser(
            @PathVariable Integer localityId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        LocalityRatings rating = localityRatingService
                .getLocalityRatingOfUser(userInfo.getUserIdentifier(), localityId);
        return new ProAPISuccessResponse(rating);
    }

    /**
     * This method will create locality rating for logged in user
     * 
     * @param localityId
     * @param localityRating
     * @param userInfo
     * @return
     */
    @RequestMapping(value = { "data/v1/entity/user/locality/{localityId}/rating" }, method = RequestMethod.POST)
    @ResponseBody
    @DisableCaching
    public ProAPIResponse createLocalityRating(
            @PathVariable Integer localityId,
            @RequestBody LocalityRatings localityRating,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        LocalityRatings createdRating = localityRatingService.createLocalityRating(
                userInfo.getUserIdentifier(),
                localityId,
                localityRating);
        return new ProAPISuccessResponse(createdRating);
    }
}
