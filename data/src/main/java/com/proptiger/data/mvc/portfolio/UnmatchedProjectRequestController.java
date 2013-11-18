package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.UnmatchedProjectRequestService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/unmatched-project")
public class UnmatchedProjectRequestController {
	
	@Autowired
	private UnmatchedProjectRequestService unmatchedProjectRequestService;
	
	public ProAPIResponse submitUnmatchedProjectDetails(){
		
		return new  ProAPISuccessResponse();
	}
	
}
