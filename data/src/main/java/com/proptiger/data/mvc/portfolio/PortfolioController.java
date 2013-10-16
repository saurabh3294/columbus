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
import com.proptiger.data.model.portfolio.PortfolioListing;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.PortfolioService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioController {

	@Autowired
	private PortfolioService portfolioService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getPortfolio(@PathVariable Integer userId) {

		Portfolio portfolio = portfolioService.getPortfolioByUserId(userId);
		return new ProAPISuccessResponse(portfolio, 1);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ProAPIResponse createPortfolio(@PathVariable Integer userId, @RequestBody Portfolio portfolio) {
		Portfolio created = portfolioService.createPortfolio(userId, portfolio);
		return new ProAPISuccessResponse(created, 1);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public ProAPIResponse updatePortfolio(@PathVariable Integer userId, @RequestBody Portfolio portfolio) {
		Portfolio updated = portfolioService.updatePortfolio(userId, portfolio);
		return new ProAPISuccessResponse(updated, 1);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listing")
	@ResponseBody
	public ProAPIResponse getAllListings(@PathVariable Integer userId) {
		List<PortfolioListing> properties = portfolioService.getAllPortfolioListings(userId);
		return new ProAPISuccessResponse(properties, properties.size());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listing/{listingId}")
	@ResponseBody
	public ProAPIResponse getOneListing(@PathVariable Integer userId,
			@PathVariable Integer listingId) {
		PortfolioListing property = portfolioService.getPortfolioListingById(userId, listingId);
		return new ProAPISuccessResponse(property, 1);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/listing")
	@ResponseBody
	public ProAPIResponse createListing(@PathVariable Integer userId,
			@RequestBody PortfolioListing portfolioProperty) {
		PortfolioListing created = portfolioService.createPortfolioListing(userId, portfolioProperty);
		return new ProAPISuccessResponse(created, 1);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}")
	@ResponseBody
	public ProAPIResponse updateListing(@PathVariable Integer userId, @PathVariable Integer listingId,
			@RequestBody PortfolioListing portfolioProperty) {
		PortfolioListing property = portfolioService.updatePortfolioListing(userId, listingId, portfolioProperty);
		return new ProAPISuccessResponse(property, 1);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/listing/{listingId}")
	@ResponseBody
	public ProAPIResponse updateListing(@PathVariable Integer userId, @PathVariable Integer listingId
			) {
		PortfolioListing property = portfolioService.deletePortfolioListing(userId, listingId);
		return new ProAPISuccessResponse(property, 1);
	}
	
}
