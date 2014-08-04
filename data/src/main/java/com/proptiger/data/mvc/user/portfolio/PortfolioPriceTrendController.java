package com.proptiger.data.mvc.user.portfolio;

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
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.portfolio.PortfolioPriceTrendService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioPriceTrendController extends BaseController {

    @Autowired
    private PortfolioPriceTrendService portfolioPriceTrendService;

    /**
     * Gets price trend for portfolio
     * 
     * @param userId
     * @param months
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/price-trend")
    @ResponseBody
    public APIResponse getPortfolioPriceTrend(@PathVariable Integer userId, @RequestParam(
            required = false,
            defaultValue = "3") Integer months, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @ModelAttribute FIQLSelector selector) {

        PortfolioPriceTrend priceTrend = portfolioPriceTrendService.getPortfolioPriceTrend(
                userInfo.getUserIdentifier(),
                months, selector);
        return new APIResponse(priceTrend.getProjectPriceTrend(), priceTrend.getProjectPriceTrend()
                .size());
    }

    /**
     * Get price trend for a listing
     * 
     * @param userId
     * @param months
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/listing/{listingId}/price-trend")
    @ResponseBody
    public APIResponse getPortfolioListingPriceTrend(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = false, defaultValue = "3") Integer months,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {

        ProjectPriceTrend priceTrend = portfolioPriceTrendService.getListingPriceTrend(
                userInfo.getUserIdentifier(),
                listingId,
                months);
        return new APIResponse(priceTrend);
    }
}
