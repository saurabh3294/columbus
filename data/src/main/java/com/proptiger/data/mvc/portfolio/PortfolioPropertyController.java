package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioProperty;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.PortfolioPropertyService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioPropertyController {

	@Autowired
	private PortfolioPropertyService proptigerPropertyService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getPortfolio(@PathVariable Integer userId) {

		Portfolio portfolio = proptigerPropertyService.getPortfolioByUserId(userId);
		return new ProAPISuccessResponse(portfolio, 1);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/property")
	@ResponseBody
	public ProAPIResponse getAllProperty(@PathVariable Integer userId) {
		List<PortfolioProperty> properties = proptigerPropertyService.getAllProperties(userId);
		return new ProAPISuccessResponse(properties, properties.size());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/property/{propertyId}")
	@ResponseBody
	public ProAPIResponse getProperty(@PathVariable Integer userId,
			@PathVariable Integer propertyId) {
		PortfolioProperty property = proptigerPropertyService.getPropertyByUserIdAndPropertyId(userId, propertyId);
		return new ProAPISuccessResponse(property, 1);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/property")
	@ResponseBody
	public ProAPIResponse createProperty(@PathVariable Integer userId,
			@RequestBody PortfolioProperty portfolioProperty) {
		PortfolioProperty created = proptigerPropertyService.createPortfolio(userId, portfolioProperty);
		return new ProAPISuccessResponse(created, 1);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/property/{propertyId}")
	@ResponseBody
	public ProAPIResponse updateProperty(@PathVariable Integer userId, @PathVariable Integer propertyId,
			@RequestBody PortfolioProperty portfolioProperty) {
		PortfolioProperty property = proptigerPropertyService.updatePortfolio(userId, propertyId, portfolioProperty);
		return new ProAPISuccessResponse(property, 1);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/property/{propertyId}")
	@ResponseBody
	public ProAPIResponse deleteProperty(@PathVariable Integer userId, @PathVariable Integer propertyId
			) {
		PortfolioProperty property = proptigerPropertyService.deleteProperty(userId, propertyId);
		return new ProAPISuccessResponse(property, 1);
	}
	
}
