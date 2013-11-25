/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityReviewService;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value = "app/v1/locality-reviews")
public class AppLoclaityReviewsController {
    @Autowired
    private LocalityReviewService localityReviewService;
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId,@RequestParam(required = false) Integer numberOfReviews){
        if(localityId == null || localityId < 1)
            return new ProAPIErrorResponse("Error", "Enter Valid Locality Id");
        
        Pageable pageable = null;
        if(numberOfReviews != null && numberOfReviews > 0)
            pageable = new PageRequest(0, numberOfReviews);
                    
        Object list = localityReviewService.findReviewByLocalityId(localityId, pageable);
        return new ProAPISuccessResponse(list);
    }
}
