package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.portfolio.PortfolioProperty;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.PortfolioPropertyService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio/property")
public class PortfolioPropertyController {

	@Autowired
	private PortfolioPropertyService proptigerPropertyService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getProperties(@PathVariable Integer userId) {

		List<PortfolioProperty> list = proptigerPropertyService.getProperties(null);
		return new ProAPISuccessResponse(list, list.size());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{propertyId}")
	@ResponseBody
	public ProAPIResponse getProperty(@PathVariable String userId,
			@PathVariable Integer propertyId) {

		List<PortfolioProperty> list = proptigerPropertyService.getProperties(propertyId);
		return new ProAPISuccessResponse(list, list.size());
	}
	
}
