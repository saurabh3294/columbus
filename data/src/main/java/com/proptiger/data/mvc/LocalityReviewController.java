package com.proptiger.data.mvc;

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
 * Controller for fetching data related to locality review
 * 
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/locality-review")
public class LocalityReviewController {

	@Autowired
	private LocalityReviewService localityReviewService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId, @RequestParam(required = false) Integer numberOfReviews){
            
            if(localityId == null || localityId < 1)
                return new ProAPIErrorResponse("Error", "Enter Valid Locality Id");
            
            System.out.println("***************"+numberOfReviews);
            Pageable pageable = null;
            if(numberOfReviews != null && numberOfReviews > 0)
                pageable = new PageRequest(0, numberOfReviews);
            else
                pageable = new PageRequest(0, 5);
            
            
            Object list = localityReviewService.findReviewByLocalityId(localityId, pageable);
            return new ProAPISuccessResponse(list);
	}
	
}
