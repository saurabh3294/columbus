/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.LocalityReviewComments.LocalityReviewRatingDetails;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.LocalityReviewService;

/**
 * 
 * @author mukand
 */
@Controller
@RequestMapping(value = "app/v1/locality-reviews")
public class AppLocalityReviewsController {
    @Autowired
    private LocalityReviewService localityReviewService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @DisableCaching
    public APIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId, @RequestParam(
            required = false) Integer numberOfReviews) {
        LocalityReviewRatingDetails reviewRatingDetails = localityReviewService.getLocalityReviewRatingDetails(
                localityId,
                numberOfReviews);
        return new APIResponse(reviewRatingDetails);
    }
}
