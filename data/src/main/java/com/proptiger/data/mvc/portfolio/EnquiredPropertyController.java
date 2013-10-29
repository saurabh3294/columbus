package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.EnquiredPropertyService;

/**
 * Providing API to get enquired property of user
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/enquired-property")
public class EnquiredPropertyController {

	@Autowired
	private EnquiredPropertyService enquiredPropertyService;
	
	
	public ProAPIResponse getEnquiredProperties(){
		
		return new ProAPISuccessResponse();
	}
}
