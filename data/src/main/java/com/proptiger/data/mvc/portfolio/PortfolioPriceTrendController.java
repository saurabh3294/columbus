package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.PortfolioPriceTrend;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.PortfolioPriceTrendService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioPriceTrendController extends BaseController{

	@Autowired
	private PortfolioPriceTrendService portfolioPriceTrendService;
	
	/**
	 * Gets price trend for portfolio
	 * @param userId
	 * @param months
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/price-trend")
	@ResponseBody
	public ProAPIResponse getPortfolioPriceTrend(@PathVariable Integer userId,
			@RequestParam(required = false, defaultValue = "3") Integer months,
			@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
		
		PortfolioPriceTrend priceTrend = portfolioPriceTrendService
				.getPortfolioPriceTrend(userInfo.getUserIdentifier(), months);
		return new ProAPISuccessCountResponse(priceTrend.getProjectPriceTrend(), priceTrend.getProjectPriceTrend().size());
	}
	
	/**
	 * Get price trend for a listing
	 * @param userId
	 * @param months
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/listing/{listingId}/price-trend")
	@ResponseBody
	public ProAPIResponse getPortfolioListingPriceTrend(@PathVariable Integer userId,
			@PathVariable Integer listingId,
			@RequestParam(required = false, defaultValue = "3") Integer months,
			@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
		
		ProjectPriceTrend priceTrend = portfolioPriceTrendService
				.getListingPriceTrend(userInfo.getUserIdentifier(), listingId, months);
		return new ProAPISuccessResponse(priceTrend);
	}
}
