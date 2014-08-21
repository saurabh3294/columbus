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
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.marketplace.LeadOfferService;
import com.proptiger.data.util.Constants;

@Controller
public class LeadOfferController extends BaseController {
    
    @Autowired 
    private LeadOfferService leadOfferService;

    @RequestMapping(value = "data/v1/entity/user/lead-offer")
    @ResponseBody
    public APIResponse get(@ModelAttribute FIQLSelector selector, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {        
        PaginatedResponse<List<LeadOffer>> paginatedResponse = leadOfferService.getLeadOffers(activeUser.getUserIdentifier(),selector);
        return new APIResponse(super.filterFieldsFromSelector(paginatedResponse.getResults(), selector), paginatedResponse.getTotalCount());
    }

    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}/listing", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse offerListing(@RequestBody List<Integer> listingIds, @PathVariable int leadOfferId) {
        return new APIResponse(leadOfferService.offerListings(listingIds, leadOfferId));
    }

    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}/listings")
    @ResponseBody
    public APIResponse getListings(@PathVariable int leadOfferId) {
        return new APIResponse(leadOfferService.getListings(leadOfferId));
    }
}
