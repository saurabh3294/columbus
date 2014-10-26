/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewRatingDetails;
import com.proptiger.data.service.LocalityReviewService;

/**
 * 
 * @author mukand
 */
@Controller
public class AppLocalityReviewsController {
    @Autowired
    private LocalityReviewService localityReviewService;

    @RequestMapping("app/v1/locality-reviews")
    @ResponseBody
    @DisableCaching
    public APIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId, @RequestParam(
            required = false) Integer numberOfReviews) {
        LocalityReviewRatingDetails reviewRatingDetails = localityReviewService.getLocalityReviewRatingDetails(
                localityId,
                numberOfReviews);
        localityReviewService.updateReviewAndRatingsByHalf(reviewRatingDetails);
        return new APIResponse(reviewRatingDetails);
    }
    
    @RequestMapping("app/v2/locality-reviews")
    @ResponseBody
    @DisableCaching
    public APIResponse getLocalityReviewByLocalityIdV2(@RequestParam Integer localityId, @RequestParam(
            required = false) Integer numberOfReviews) {
        LocalityReviewRatingDetails reviewRatingDetails = localityReviewService.getLocalityReviewRatingDetails(
                localityId,
                numberOfReviews);
        return new APIResponse(reviewRatingDetails);
    }
}
