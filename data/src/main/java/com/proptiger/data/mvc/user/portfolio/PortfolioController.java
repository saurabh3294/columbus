package com.proptiger.data.mvc.user.portfolio;

import java.util.List;
import java.util.Set;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;

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

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.ListingStatus;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.model.Subscription;
import com.proptiger.data.model.user.portfolio.Portfolio;
import com.proptiger.data.service.user.SubscriptionService;
import com.proptiger.data.service.user.portfolio.PortfolioService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio")
public class PortfolioController extends BaseController {

    @Autowired
    private PortfolioService    portfolioService;
    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getPortfolio(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "ACTIVE", value = "listingStatus") List<ListingStatus> listingStatus,
            @RequestParam(required = false, value = "selector") String selectorStr,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {

        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        Portfolio portfolio = portfolioService.getPortfolioByUserId(userInfo.getUserIdentifier(), listingStatus);
        Set<String> fields = null;
        if (selector != null) {
            fields = selector.getFields();
        }
        return new APIResponse(super.filterFields(portfolio, fields), 1);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listing")
    @ResponseBody
    public APIResponse getAllListings(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "ACTIVE", value = "listingStatus") List<ListingStatus> listingStatus,
            @RequestParam(required = false, value = "selector") String selectorStr,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);

        List<PortfolioListing> listings = portfolioService.getAllPortfolioListings(
                userInfo.getUserIdentifier(),
                listingStatus);
        Set<String> fields = null;
        if (selector != null) {
            fields = selector.getFields();
        }
        return new APIResponse(super.filterFields(listings, fields), listings.size());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listing/{listingId}")
    @ResponseBody
    public APIResponse getOneListing(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = false, value = "selector") String selectorStr,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        PortfolioListing listing = portfolioService.getPortfolioListingById(userInfo.getUserIdentifier(), listingId);
        Set<String> fields = null;
        if (selector != null) {
            fields = selector.getFields();
        }
        return new APIResponse(super.filterFields(listing, fields), 1);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listing")
    @ResponseBody
    public APIResponse createListing(
            HttpServletRequest request,
            @PathVariable Integer userId,
            @RequestBody PortfolioListing portfolioProperty,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        /*
         * Setting user-agent to the portfolio-listing to track the platform
         * info for analysis purpose.
         */
        setUserAgent(request, portfolioProperty);
        PortfolioListing created = portfolioService.createPortfolioListing(
                userInfo.getUserIdentifier(),
                portfolioProperty);
        /*
         * Calling this ListingById method to update current listing with price and other details 
         */
        created = portfolioService.getPortfolioListingById(userId, created.getId());
        return new APIResponse(super.filterFields(created, null));
    }

    private void setUserAgent(HttpServletRequest request, PortfolioListing portfolioProperty) {
        String userAgent = request.getHeader(Constants.USER_AGENT);
        if (userAgent != null && !userAgent.isEmpty()) {
            portfolioProperty.setUserAgent(userAgent);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @ResponseBody
    public APIResponse updateListing(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestBody PortfolioListing portfolioProperty,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        portfolioService.updatePortfolioListing(userInfo.getUserIdentifier(), listingId, portfolioProperty);
        PortfolioListing updatedListing = portfolioService.getPortfolioListingById(userId, listingId);
        return new APIResponse(super.filterFields(updatedListing, null));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/listing/{listingId}")
    @ResponseBody
    public APIResponse deleteListing(@PathVariable Integer userId, @PathVariable Integer listingId, 
            @RequestParam(required = false,value = "reason") String reason, 
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        PortfolioListing listing = portfolioService.deletePortfolioListing(
                userInfo.getUserIdentifier(),
                listingId,
                reason);
        return new APIResponse(super.filterFields(listing, null));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}/interested-to-sell")
    @ResponseBody
    public APIResponse interestedToSell(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = true, value = "interestedToSell") Boolean interestedToSell,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        PortfolioListing listing = portfolioService.interestedToSellListing(
                userInfo.getUserIdentifier(),
                listingId,
                interestedToSell);
        return new APIResponse(super.filterFields(listing, null));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/listing/{listingId}/loan-request")
    @ResponseBody
    public APIResponse interestedToHomeLoan(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = false, defaultValue = "true", value = "loan") Boolean interestedToLoan,
            @RequestParam(required = false, value = "loanType") String loanType,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        PortfolioListing listing = portfolioService.interestedToHomeLoan(
                userInfo.getUserIdentifier(),
                listingId,
                interestedToLoan,
                loanType);
        return new APIResponse(super.filterFields(listing, null));
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
    public APIResponse sendMailForListing(@PathVariable Integer userId, @PathVariable Integer listingId, 
            @RequestParam(
            required = true,
            value = "mailType") String mailType, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        boolean status = portfolioService.handleMailRequest(userInfo.getUserIdentifier(), listingId, mailType);
        return new APIResponse(status);
    }

    /**
     * @param userId
     * @param listingId
     * @param unsubscribeTypes
     * @param userInfo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/listing/{listingId}/unsubscribe")
    @ResponseBody
    public APIResponse unsubscribe(
            @PathVariable Integer userId,
            @PathVariable Integer listingId,
            @RequestParam(required = true, value = "unsubscribeTypes") String[] unsubscribeTypes,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<Subscription> subscriptions = subscriptionService.disableSubscription(
                userId,
                listingId,
                PortfolioListing.class.getAnnotation(Table.class).name(),
                unsubscribeTypes);
        return new APIResponse(subscriptions, subscriptions.size());
    }
}