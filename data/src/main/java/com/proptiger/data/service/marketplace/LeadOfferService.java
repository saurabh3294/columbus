package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOfferedListing;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.seller.CompanyUser;
import com.proptiger.data.model.user.User;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingsDao;
import com.proptiger.data.service.CompanyService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 * 
 */

@Service
public class LeadOfferService {
    @Autowired
    private CompanyService          companyService;

    @Autowired
    private LeadOfferDao            leadOfferDao;

    @Autowired
    private LeadRequirementsService leadRequirementsService;

    @Autowired
    private UserService             userService;

    @Autowired
    private LeadOfferedListingsDao  leadOfferedListingDao;

    @Autowired
    private LeadService             leadService;

    /**
     * 
     * @param leadOfferedListing
     * @return
     */
    public List<LeadOfferedListing> offerListings(List<Integer> listingIds, int leadOfferId) {
        List<LeadOfferedListing> leadOfferedListings = leadOfferedListingDao.findByLeadOfferIdAndListingIdIn(
                leadOfferId,
                listingIds);
        
        Set<Integer> existingListingIds = extractListingIds(leadOfferedListings);
        
        for (Integer listingId : listingIds) {
            if (!existingListingIds.contains(listingId)) {
                leadOfferedListingDao.saveAndFlush(new LeadOfferedListing(leadOfferId, listingId));
            }
        }

        return leadOfferedListings;
    }

    private Set<Integer> extractListingIds(List<LeadOfferedListing> leadOfferedListings) {
        Set<Integer> listingIds = new HashSet<>();
        
        if (leadOfferedListings != null) {
            for (LeadOfferedListing leadOfferedListing : leadOfferedListings) {
                listingIds.add(leadOfferedListing.getListingId());
            }
        }

        return listingIds;
    }

    /**
     * @param agentId
     * @param selector
     * @return
     */
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(int agentId, FIQLSelector selector) {
        if (selector == null) {
            selector = new FIQLSelector();
        }

        List<LeadOffer> leadOffers = leadOfferDao.getLeadOffersForAgent(agentId);
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>(leadOffers, leadOffers.size());

        Set<String> fields = selector.getFieldSet();
        if (fields != null) {
            if (fields.contains("client")) {
                List<Integer> clientIds = extractClientIds(paginatedResponse.getResults());
                Map<Integer, User> users = userService.getUsers(clientIds);
                for (LeadOffer leadOffer : paginatedResponse.getResults()) {
                    leadOffer.getLead().setClient(users.get(leadOffer.getLead().getClientId()));
                }
            }

            if (fields.contains("requirements")) {
                List<Integer> leadIds = extractLeadIds(paginatedResponse.getResults());
                Map<Integer, List<LeadRequirement>> requirements = getLeadRequirements(leadIds);
                for (LeadOffer leadOffer : paginatedResponse.getResults()) {
                    leadOffer.getLead().setRequirements(requirements.get(leadOffer.getId()));
                }
            }

            if (fields.contains("listings")) {
                List<Integer> leadOfferIds = extractLeadOfferIds(leadOffers);
                Map<Integer, List<Listing>> listings = getLeadOfferedListing(leadOfferIds);
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setListings(listings.get(leadOffer.getId()));
                }
            }

            // XXX - Setting lead to null if not passed in fields
            if (!fields.contains("lead")) {
                for (LeadOffer leadOffer : paginatedResponse.getResults()) {
                    leadOffer.setLead(null);
                }
            }
        }

        return paginatedResponse;
    }

    private Map<Integer, List<LeadRequirement>> getLeadRequirements(List<Integer> leadIds) {
        Map<Integer, List<LeadRequirement>> requirementsMap = new HashMap<>();
        for (LeadRequirement leadRequirement : leadRequirementsService.getRequirements(leadIds)) {
            int leadId = leadRequirement.getLeadId();
            if (!requirementsMap.containsKey(leadId)) {
                requirementsMap.put(leadId, new ArrayList<LeadRequirement>());
            }

            requirementsMap.get(leadId).add(leadRequirement);
        }

        return requirementsMap;
    }

    private List<Integer> extractLeadIds(List<LeadOffer> leadOffers) {
        List<Integer> leadIds = new ArrayList<Integer>();
        for (LeadOffer leadOffer : leadOffers) {
            leadIds.add(leadOffer.getLeadId());
        }
        return leadIds;
    }

    private List<Integer> extractClientIds(List<LeadOffer> leadOffers) {
        List<Integer> clientIds = new ArrayList<Integer>();
        for (LeadOffer leadOffer : leadOffers) {
            clientIds.add(leadOffer.getLead().getClientId());
        }
        return clientIds;
    }


    
    
      
    private Map<Integer, List<Listing>> getLeadOfferedListing(List<Integer> leadOfferIds) {
        Map<Integer, List<Listing>> listingMap = new HashMap<>();
        for (LeadOffer.LeadOfferIdListing leadOfferIdListing  : leadOfferDao.getListings(leadOfferIds)) {
            int leadOfferId = leadOfferIdListing.getLeadOfferId();
            if (!listingMap.containsKey(leadOfferId)) {
                listingMap.put(leadOfferId, new ArrayList<Listing>());
            }            
            listingMap.get(leadOfferId).add(leadOfferIdListing.getListing());
        }
        return listingMap;
    }

    private List<Integer> extractLeadOfferIds(List<LeadOffer> leadOffers) {
        List<Integer> leadOfferIds = new ArrayList<>();
        for (LeadOffer leadOffer : leadOffers) {
            leadOfferIds.add(leadOffer.getId());
        }
        return leadOfferIds;
    }

    public LeadOffer offerLeadToBroker(Lead lead, Company brokerCompany, int cycleId) {
        List<CompanyUser> agents = companyService.getCompanyUsersForCompanies(brokerCompany);
        if (agents.size() == 0) {
            throw new ProAPIException("No Agent Found For Broker");
        }
        else {
            return createLeadOffer(lead, agents.get(0));
        }
    }

    public LeadOffer createLeadOffer(Lead lead, CompanyUser agent) {
        LeadOffer offer = new LeadOffer();
        offer.setLeadId(lead.getId());
        offer.setAgentId(agent.getId());
        offer.setStatusId(LeadOfferStatus.Offered.getLeadOfferStatusId());
        offer.setCycleId(1);
        return leadOfferDao.save(offer);
    }

    /*public PaginatedResponse<List<Listing>> getListings(int leadOfferId) {
        List<LeadOffer> leadOffers = leadOfferDao.getListings(Collections.singletonList(leadOfferId));
        return new PaginatedResponse<List<Listing>>(leadOffers.get(0).getListings(), leadOffers.get(0).getListings().size());
    }*/
}




