package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.ReviewComments;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.ProAPIErrorResponse;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.util.Constants;

/**
 * Controller for fetching data related to locality review
 * 
 * @author Rajeev Pandey
 *
 */
@Controller
public class LocalityReviewController extends BaseController{

	@Autowired
	private LocalityReviewService localityReviewService;
	
	@RequestMapping(value = "data/v1/entity/locality-review", method = RequestMethod.GET)
	@ResponseBody
	@DisableCaching
	public ProAPIResponse getLocalityReviewByLocalityId(@RequestParam Integer localityId, @RequestParam(required = false) Integer numberOfReviews){
            
            if(localityId == null || localityId < 1)
                return new ProAPIErrorResponse("Error", "Enter Valid Locality Id");
            
            Pageable pageable = null;
            if(numberOfReviews != null && numberOfReviews > 0)
                pageable = new LimitOffsetPageRequest(0, numberOfReviews);
        
            Object list = localityReviewService.findReviewByLocalityId(localityId, pageable);
            return new ProAPISuccessResponse(list);
	}
	
	@RequestMapping(value = "data/v1/entity/user/locality/{localityId}/review", method = RequestMethod.POST)
	@ResponseBody
	@DisableCaching
	public ProAPIResponse createReview(
			@RequestParam ReviewComments reviewComments,
			@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {

		return null;
	}
}
