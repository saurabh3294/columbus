package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.portfolio.Portfolio;
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
	public ProAPIResponse getPortfolio(@PathVariable String userId){
		List<Portfolio> list = portfolioService.getPortfolio(null);
		return new ProAPISuccessResponse(list, list.size());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{portfolioId}")
	@ResponseBody
	public ProAPIResponse getPortfolioById(@PathVariable String userId, @PathVariable Integer portfolioId){
		List<Portfolio> list = portfolioService.getPortfolio(portfolioId);
		return new ProAPISuccessResponse(list, list.size());
	}
	
}
