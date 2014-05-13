package com.proptiger.data.mvc.portfolio;

import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.Subscription;
import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioListing;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.portfolio.PortfolioService;
import com.proptiger.data.service.portfolio.SubscriptionService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioController extends BaseController {

    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getPortfolio(
            @PathVariable Integer userId,
            @RequestParam(required = false, value = "selector") String selectorStr,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {

        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        Portfolio portfolio = portfolioService.getPortfolioByUserId(userInfo.getUserIdentifier());
        Set<String> fields = null;
        if (selector != null) {
            fields = selector.getFields();
        }
        return new ProAPISuccessCountResponse(super.filterFieldsWithTree(portfolio, fields), 1);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public ProAPIResponse createPortfolio(
            @PathVariable Integer userId,
            @RequestBody Portfolio portfolio,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Portfolio created = portfolioService.createPortfolio(userInfo.getUserIdentifier(), portfolio);
        return new ProAPISuccessCountResponse(super.filterFieldsWithTree(created, null), 1);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @ResponseBody
    public ProAPIResponse updatePortfolio(
            @PathVariable Integer userId,
            @RequestBody Portfolio portfolio,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Portfolio updated = portfolioService.updatePortfolio(userInfo.getUserIdentifier(), portfolio);
        return new ProAPISuccessResponse(updated);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listing")
    @ResponseBody
    public ProAPIResponse getAllListings(@PathVariable Integer userId, @RequestParam(
            required = false,
            value = "selector") String selectorStr, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);

        List<PortfolioListing> listings = portfolioService.getAllPortfolioListings(userInfo.getUserIdentifier());
        Set<String> fields = null;
        if (selector != null) {
            fields = selector.getFields();
        }
        return new ProAPISuccessCountResponse(super.filterFields(listings, fields), listings.size());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listing/{listingId}")
    @ResponseBody
    public ProAPIResponse getOneListing(@PathVariable Integer userId, @PathVariable Integer listingId, @RequestParam(
            required = false,
            value = "selector") String selectorStr, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        PortfolioListing listing = portfolioService.getPortfolioListingById(userInfo.getUserIdentifier(), listingId);
        Set<String> fields = null;
        if (selector != null) {
            fields = selector.getFields();
        }
        return new ProAPISuccessCountResponse(super.filterFieldsWithTree(listing, fields), 1);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listing")
    @ResponseBody
    public ProAPIResponse createListing(
            @PathVariable Integer userId,
            @RequestBody PortfolioListing portfolioProperty,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        PortfolioListing created = portfolioService.createPortfolioListing(
                userInfo.getUserIdentifier(),
                portfolioProperty);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(created, null));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @ResponseBody
    public ProAPIResponse updateListing(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestBody PortfolioListing portfolioProperty,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        portfolioService.updatePortfolioListing(
                userInfo.getUserIdentifier(),
                listingId,
                portfolioProperty);
        PortfolioListing updatedListing = portfolioService.getPortfolioListingById(userId, listingId);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(updatedListing, null));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/listing/{listingId}")
    @ResponseBody
    public ProAPIResponse deleteListing(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = false, value = "reason") String reason, 
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        PortfolioListing listing = portfolioService.deletePortfolioListing(userInfo.getUserIdentifier(), listingId , reason);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(listing, null));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}/interested-to-sell")
    @ResponseBody
    public ProAPIResponse interestedToSell(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = true, value = "interestedToSell") Boolean interestedToSell,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        PortfolioListing listing = portfolioService.interestedToSellListing(
                userInfo.getUserIdentifier(),
                listingId,
                interestedToSell);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(listing, null));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}/loan-request")
    @ResponseBody
    public ProAPIResponse interestedToHomeLoan(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = false, defaultValue = "true", value = "loan") Boolean interestedToLoan,
            @RequestParam(required = false, value = "loanType") String loanType,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        PortfolioListing listing = portfolioService.interestedToHomeLoan(
                userInfo.getUserIdentifier(),
                listingId,
                interestedToLoan,
                loanType);
        return new ProAPISuccessResponse(super.filterFieldsWithTree(listing, null));
    }

    /**
     * This method send various types of mail related to a listing object.
     * 
     * @param userId
     * @param listingId
     * @param mailType
     * @param userInfo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/listing/{listingId}/mail")
    @ResponseBody
    public ProAPIResponse sendMailForListing(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = true, value = "mailType") String mailType,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        boolean status = portfolioService.handleMailRequest(userInfo.getUserIdentifier(), listingId, mailType);
        return new ProAPISuccessResponse(status);
    }

    /**
     * @param userId
     * @param listingId
     * @param unsubscribeTypes
     * @param userInfo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value ="/listing/{listingId}/unsubscribe")
    @ResponseBody
    public ProAPIResponse unsubscribe(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = true, value = "unsubscribeTypes") String[] unsubscribeTypes,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<Subscription> subscriptions = subscriptionService.disableSubscription(userId, listingId, PortfolioListing.class
                .getAnnotation(Table.class).name(), unsubscribeTypes);
        return new ProAPISuccessCountResponse(subscriptions, subscriptions.size());
    }
}
