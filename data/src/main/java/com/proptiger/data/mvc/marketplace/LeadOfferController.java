package com.proptiger.data.mvc.marketplace;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.SenderDetail;
import com.proptiger.data.model.Listing;
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
    public APIResponse get(
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser,
            @RequestParam(required = false) List<Integer> statusIds,@RequestParam(required = false) String dueDate) {
        PaginatedResponse<List<LeadOffer>> paginatedResponse = leadOfferService.getLeadOffers(
                activeUser.getUserIdentifier(),
                selector, statusIds, dueDate);
        return new APIResponse(paginatedResponse.getResults(), paginatedResponse.getTotalCount());
    }

    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}/offered-listings")
    @ResponseBody
    public APIResponse getOfferedListings(
            @PathVariable int leadOfferId,
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        PaginatedResponse<List<Listing>> offeredListings = leadOfferService.getOfferedListings(
                leadOfferId);
        return new APIResponse(
                super.filterFieldsFromSelector(offeredListings.getResults(), selector),
                offeredListings.getTotalCount());
    }

    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}/matching-listings")
    @ResponseBody
    public APIResponse getMatchingListings(
            @PathVariable int leadOfferId,
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        PaginatedResponse<List<Listing>> matchingListings = leadOfferService.getSortedMatchingListings(leadOfferId);
        return new APIResponse(
                super.filterFieldsFromSelector(matchingListings.getResults(), selector),
                matchingListings.getTotalCount());
    }

    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}")
    @ResponseBody
    public APIResponse get(
            @PathVariable int leadOfferId,
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        return new APIResponse(leadOfferService.get(leadOfferId, activeUser.getUserIdentifier(), selector));
    }

    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse update(
            @RequestBody LeadOffer leadOffer,
            @PathVariable int leadOfferId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        return new APIResponse(leadOfferService.updateLeadOffer(leadOffer, leadOfferId, activeUser.getUserIdentifier()));
    }
    
    @RequestMapping(value = "data/v1/entity/user/lead-offer/{leadOfferId}/email", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateLeadOfferForEmailTask(
            @RequestBody SenderDetail senderDetails,
            @PathVariable int leadOfferId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        LeadOffer leadOffer = leadOfferService.updateLeadOfferForEmailTask(leadOfferId, activeUser, senderDetails);
        return new APIResponse(leadOffer);
    }
}
