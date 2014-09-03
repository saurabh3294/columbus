package com.proptiger.data.mvc.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.Listing;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.marketplace.ListingService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/listing")
public class ListingController extends BaseController {

    @Autowired
    private ListingService listingService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public APIResponse createListing(
            @RequestBody Listing listing,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo, @ModelAttribute FIQLSelector selector) {
        Listing created = listingService.createListing(listing, userInfo.getUserIdentifier());
        listing = listingService.getListing(userInfo.getUserIdentifier(), created.getId(), selector);
        return new APIResponse(super.filterFieldsFromSelector(listing, selector));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public APIResponse getListings(
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        PaginatedResponse<List<Listing>> listings = listingService.getListings(userInfo.getUserIdentifier(), selector);
        return new APIResponse(super.filterFieldsFromSelector(listings.getResults(), selector), listings.getTotalCount());
    }

    @ResponseBody
    @RequestMapping(value = "{listingId}", method = RequestMethod.GET)
    public APIResponse getListing(
            @ModelAttribute FIQLSelector selector,
            @PathVariable Integer listingId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        Listing listing = listingService.getListing(userInfo.getUserIdentifier(), listingId, selector);
        return new APIResponse(super.filterFieldsFromSelector(listing, selector), 1);
    }

    @ResponseBody
    @RequestMapping(value = "{listingId}", method = RequestMethod.DELETE)
    public APIResponse deleteListing(
            @PathVariable Integer listingId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        Listing listing = listingService.deleteListing(userInfo.getUserIdentifier(), listingId);
        return new APIResponse(super.filterFields(listing, null));
    }

}
