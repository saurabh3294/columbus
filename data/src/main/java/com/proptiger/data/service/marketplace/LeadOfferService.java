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
import com.proptiger.data.model.marketplace.LeadOffer.LeadOfferIdListing;
import com.proptiger.data.model.marketplace.LeadOfferedListing;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.seller.CompanyUser;
import com.proptiger.data.model.user.User;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingsDao;
import com.proptiger.data.service.CompanyService;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.exception.BadRequestException;
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

    @Autowired
    private LeadTaskService         leadTaskService;

    @Autowired 
    private ListingService  listingService;
    
    
    /**
     * 
     * @param integer 
     * @param leadOfferedListing
     * @return
     */

    public List<Integer> offerListings(List<Integer> listingIds, int leadOfferId , int userId) {        
        List<Listing> leadValidListings = listingService.getListings(listingIds,userId);
        
        if(leadValidListings.size() != listingIds.size())
        {
            throw new BadRequestException("Some of the listings are not yours or listing id is invalid");
        }
        
        List<LeadOfferedListing> leadOfferedListings = leadOfferedListingDao.findByLeadOfferIdAndListingIdIn(leadOfferId,listingIds);
        Set<Integer> existingListingIds = extractListingIds(leadOfferedListings);
        List<Integer> newOfferedListingIds = new ArrayList<>();

        for (Integer listingId : listingIds) {
            if (!existingListingIds.contains(listingId)) {
                leadOfferedListingDao.saveAndFlush(new LeadOfferedListing(leadOfferId, listingId));
                newOfferedListingIds.add(listingId);
            }
        }

        return newOfferedListingIds;
    }

    /**
     * extracts listing ids from leadoffered listing object
     * 
     * @param leadOfferedListings
     * @return
     */

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
     * 
     * get lead function which gets leadoffer data, lead data,lead requirement
     * data, client data according to the fields field in selector
     * 
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
                    leadOffer.getLead().setRequirements(requirements.get(leadOffer.getLeadId()));
                }
            }

            if (fields.contains("listings")) {
                List<Integer> leadOfferIds = extractLeadOfferIds(leadOffers);
                Map<Integer, List<Listing>> listings = getLeadOfferedListing(leadOfferIds);
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setOfferedListings(listings.get(leadOffer.getId()));
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

    /**
     * 
     * gets lead requirements in hashmap with key leadids
     * 
     * @param leadOfferIds
     * @return
     */

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

    /**
     * 
     * extracts lead ids from leadoffer objects
     * 
     * @param leadOffers
     * @return
     */

    private List<Integer> extractLeadIds(List<LeadOffer> leadOffers) {
        List<Integer> leadIds = new ArrayList<Integer>();
        for (LeadOffer leadOffer : leadOffers) {
            leadIds.add(leadOffer.getLeadId());
        }
        return leadIds;
    }

    /**
     * extracts clientIds from list of leadoffer objects
     * 
     * @param leadOffers
     * @return
     */

    private List<Integer> extractClientIds(List<LeadOffer> leadOffers) {
        List<Integer> clientIds = new ArrayList<Integer>();
        for (LeadOffer leadOffer : leadOffers) {
            clientIds.add(leadOffer.getLead().getClientId());
        }
        return clientIds;
    }

    /**
     * 
     * gets lead offer listings in hashmap with key leadofferids
     * 
     * @param leadOfferIds
     * @return
     */

    private Map<Integer, List<Listing>> getLeadOfferedListing(List<Integer> leadOfferIds) {
        Map<Integer, List<Listing>> listingMap = new HashMap<>();
        for (LeadOffer.LeadOfferIdListing leadOfferIdListing : leadOfferDao.getListings(leadOfferIds)) {
            int leadOfferId = leadOfferIdListing.getLeadOfferId();
            if (!listingMap.containsKey(leadOfferId)) {
                listingMap.put(leadOfferId, new ArrayList<Listing>());
            }
            listingMap.get(leadOfferId).add(leadOfferIdListing.getListing());
        }
        return listingMap;
    }

    /**
     * extract list of leadOfferIds from List of leadOffer Objects
     * 
     * @param leadOffers
     * @return
     */

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

    /**
     * 
     * @param leadOfferId
     * @param integer 
     * @return listings for that lead offer id
     */
    public PaginatedResponse<List<Listing>> getOfferedListings(int leadOfferId) {
        List<Listing> listings = new ArrayList<>();
        for (LeadOffer.LeadOfferIdListing leadOfferIdListing : leadOfferDao.getListings(Collections
                .singletonList(leadOfferId))) {
            listings.add(leadOfferIdListing.getListing());
        }

        return new PaginatedResponse<List<Listing>>(listings, listings.size());
    }

    /**
     * Finds entry for logged in agent for that lead offer id and updates only
     * status field in marketplace.lead_offer only when previous status is
     * offered and changing it to new or decline.
     * 
     * @param leadOffer
     * @param leadOfferId
     * @param userId
     * @return
     */

    public LeadOffer updateLeadOffer(LeadOffer leadOffer, int leadOfferId, int userId) {
        LeadOffer leadOfferInDB = leadOfferDao.findByIdAndAgentId(leadOfferId, userId);
        if (leadOfferInDB == null) {
            throw new BadRequestException("Invalid lead offer");
        }

        if (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered.getLeadOfferStatusId()) {
            if (leadOffer.getStatusId() == LeadOfferStatus.New.getLeadOfferStatusId()) {
                leadTaskService.createDefaultLeadTaskForLeadOffer(leadOfferInDB);
                leadOfferInDB.setStatusId(leadOffer.getStatusId());
            }

            if (leadOffer.getStatusId() == LeadOfferStatus.Declined.getLeadOfferStatusId()) {
                leadOfferInDB.setStatusId(leadOffer.getStatusId());
            }
        }

        leadOfferDao.save(leadOfferInDB);
        return leadOfferInDB;
    }

    public PaginatedResponse<?> getListingsOfUser(int leadOfferId, Integer userId) {        
                
        List<Listing> leadValidListings = leadOfferDao.getListingByUserId(leadOfferId,userId);
        
        return new PaginatedResponse<List<Listing>>(leadValidListings, leadValidListings.size());
    }
   /**
     * utility method for updating lead offer status
     * 
     * @param leadOfferId
     * @param statusId
     * @return
     */
    public LeadOffer updateLeadOfferStatus(int leadOfferId, int statusId) {
        LeadOffer leadOffer = leadOfferDao.findOne(leadOfferId);
        leadOffer.setStatusId(statusId);
        leadOffer = leadOfferDao.save(leadOffer);
        return leadOffer;
    }

    public PaginatedResponse<List<Listing>> getMatchingListings(int leadOfferId) {
        List<Listing> listings = leadOfferDao.getMatchingListings(leadOfferId);
        List<LeadRequirement> leadRequirements = leadRequirementsService.getRequirements(leadOfferId);
        listings = sortMatchingListings(listings, leadRequirements);
        return new PaginatedResponse<List<Listing>>(listings, listings.size());
    }

    /**
     * Returns all matching listings for a lead offer ordered by relevance
     *
     * @param listings
     * @param leadRequirements
     * @return
     */
    private List<Listing> sortMatchingListings(List<Listing> listings, List<LeadRequirement> leadRequirements) {
        List<Listing> sortedList = new ArrayList<>();
        Map<Integer, List<Listing>> listingsByProjectId = new HashMap<>();
        
        for (Listing listing: listings) {
            int projectId = listing.getProperty().getProjectId();
            if (listingsByProjectId.containsKey(projectId)) {
                listingsByProjectId.put(projectId, new ArrayList<Listing>());
            }
            
            listingsByProjectId.get(projectId).add(listing);
        }

        for (LeadRequirement leadRequirement : leadRequirements) {
            Integer projectId = leadRequirement.getProjectId();
            if (listingsByProjectId.containsKey(projectId)) {
                sortedList.addAll(listingsByProjectId.get(projectId));
                listingsByProjectId.remove(projectId);
            }
        }

        for (List<Listing> remainingListings : listingsByProjectId.values()) {
            sortedList.addAll(remainingListings);
        }

        return sortedList;
    }

    public LeadOffer get(int leadOfferId, Integer userIdentifier) {
        return leadOfferDao.findOne(leadOfferId);
    }
}
