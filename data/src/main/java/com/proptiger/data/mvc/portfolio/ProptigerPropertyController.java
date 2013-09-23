package com.proptiger.data.mvc.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/{userId}/portfolio/{portfolioId}/property")
public class ProptigerPropertyController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getProperties(@PathVariable String userId,
			@PathVariable String portfolioId) {

		return new ProAPISuccessResponse();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{propertyId}")
	@ResponseBody
	public ProAPIResponse getProperty(@PathVariable String userId,
			@PathVariable String portfolioId, @PathVariable String propertyId) {

		return new ProAPISuccessResponse();
	}
}
