package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioListing;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.portfolio.PortfolioService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioController extends BaseController{

	@Autowired
	private PortfolioService portfolioService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getPortfolio(
			@PathVariable Integer userId,
			@RequestParam(required = false, value = "selector") String selectorStr) {

		Selector selector = super
				.parseJsonToObject(selectorStr, Selector.class);
		Portfolio portfolio = portfolioService.getPortfolioByUserId(userId);
		return postProcess(portfolio, 1, selector);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public ProAPIResponse createPortfolio(@PathVariable Integer userId, @RequestBody Portfolio portfolio) {
		Portfolio created = portfolioService.createPortfolio(userId, portfolio);
		return new ProAPISuccessResponse(created);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@ResponseBody
	public ProAPIResponse updatePortfolio(@PathVariable Integer userId, @RequestBody Portfolio portfolio) {
		Portfolio updated = portfolioService.updatePortfolio(userId, portfolio);
		return new ProAPISuccessResponse(updated);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listing")
	@ResponseBody
	public ProAPIResponse getAllListings(@PathVariable Integer userId,
			@RequestParam(required = false, value = "selector") String selectorStr) {
		Selector selector = super
				.parseJsonToObject(selectorStr, Selector.class);
		
		List<PortfolioListing> listings = portfolioService.getAllPortfolioListings(userId);
		return postProcess(listings, 1, selector);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listing/{listingId}")
	@ResponseBody
	public ProAPIResponse getOneListing(@PathVariable Integer userId,
			@PathVariable Integer listingId,
			@RequestParam(required = false, value = "selector") String selectorStr) {
		Selector selector = super
				.parseJsonToObject(selectorStr, Selector.class);
		PortfolioListing listing = portfolioService.getPortfolioListingById(userId, listingId);
		return postProcess(listing, 1, selector);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/listing")
	@ResponseBody
	public ProAPIResponse createListing(@PathVariable Integer userId,
			@Validated  @RequestBody PortfolioListing portfolioProperty) {
		PortfolioListing created = portfolioService.createPortfolioListing(userId, portfolioProperty);
		return new ProAPISuccessResponse(created);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@ResponseBody
	public ProAPIResponse updateListing(@PathVariable Integer userId, @PathVariable Integer listingId,
			@RequestBody PortfolioListing portfolioProperty) {
		PortfolioListing listing = portfolioService.updatePortfolioListing(userId, listingId, portfolioProperty);
		return new ProAPISuccessResponse(listing);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/listing/{listingId}")
	@ResponseBody
	public ProAPIResponse deleteListing(@PathVariable Integer userId, @PathVariable Integer listingId) {
		PortfolioListing listing = portfolioService.deletePortfolioListing(userId, listingId);
		return new ProAPISuccessResponse(listing);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}/interested-to-sell")
	@ResponseBody
	public ProAPIResponse interestedToSell(@PathVariable Integer userId, @PathVariable Integer listingId,
			@RequestBody(required = true) Boolean interestedToSell) {
		PortfolioListing listing = portfolioService.interestedToSellListing(userId, listingId, interestedToSell);
		return new ProAPISuccessResponse(listing);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/listing/{listingId}/mail")
	@ResponseBody
	public ProAPIResponse sendMailForListingAdd(@PathVariable Integer userId,
			@PathVariable Integer listingId, @RequestParam(required = true, value = "mailType") String mailType) {
		portfolioService.sendMail(userId, listingId, mailType);
		return new ProAPISuccessResponse("Mail Sent");
	}
	
}
