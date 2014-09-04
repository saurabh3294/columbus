package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.enums.LeadTaskName;
import com.proptiger.data.enums.LeadTaskStatusName;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.SenderDetail;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOffer.CountListingObject;
import com.proptiger.data.model.marketplace.LeadOfferedListing;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.UserContactNumber;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingDao;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.service.companyuser.CompanyService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceNotAvailableException;

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
    private LeadOfferedListingDao   leadOfferedListingDao;

    @Autowired
    private LeadService             leadService;

    @Autowired
    private LeadTaskService         leadTaskService;

    @Autowired
    private ListingService          listingService;

    @Autowired
    private LeadTaskStatusDao       leadTaskStatusDao;

    @Autowired
    private MailSender              mailSender;
    
    private String defaultSort = "nextTask.scheduledFor";

    /**
     * 
     * @param integer
     * @param leadOfferedListing
     * @return
     */
    private List<Integer> offerListings(List<Integer> listingIds, int leadOfferId, int userId) {
        List<LeadOfferedListing> leadOfferedListings = leadOfferedListingDao.findByLeadOfferIdAndListingIdIn(
                leadOfferId,
                listingIds);

        Set<Integer> existingListingIds = extractListingIds(leadOfferedListings);
        List<Integer> newOfferedListingIds = new ArrayList<>();

        Set<Integer> matchingListingIds = new HashSet<>();
        for (Listing listing : getUnsortedMatchingListings(leadOfferId).getResults()) {
            matchingListingIds.add(listing.getId());
        }

        for (int listingId : listingIds) {
            if (!existingListingIds.contains(listingId)) {
                if (matchingListingIds.contains(listingId)) {
                    leadOfferedListingDao.saveAndFlush(new LeadOfferedListing(leadOfferId, listingId));
                    newOfferedListingIds.add(listingId);
                }
                else {
                    throw new IllegalArgumentException("Trying to offer non matching listing: " + listingId);
                }
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
     * @param dueDate 
     * @param statusIds 
     * @return
     */
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(int agentId, FIQLSelector selector, List<Integer> statusIds, Date dueDate) {
        selector.applyDefSort(defaultSort);
        if(dueDate == null){
            //TODO remove this once done from fiql
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -20);
            dueDate = cal.getTime();
        }
        Pageable pageable = new LimitOffsetPageRequest(selector.getStart(), selector.getRows(), selector.getSpringDataSort());
        List<LeadOffer> leadOffers = null;
        if(statusIds == null || statusIds.size() == 0){
            leadOffers = leadOfferDao.getLeadOffersForAgentDueDateGreatherThan(agentId, dueDate, pageable);
        }
        else{
            leadOffers = leadOfferDao.getLeadOffersForAgentWhereStatusIdsIn(agentId, statusIds, dueDate, pageable);
        }
       
        PaginatedResponse<List<LeadOffer>> paginatedResponse = new PaginatedResponse<>(leadOffers, leadOffers.size());

        Set<String> fields = selector.getFieldSet();
        enrichLeadOffers(leadOffers, fields);

        return paginatedResponse;
    }

    private void enrichLeadOffers(List<LeadOffer> leadOffers, Set<String> fields) {
        if (fields != null && leadOffers != null && !leadOffers.isEmpty()) {
            if (fields.contains("client")) {
                List<Integer> clientIds = extractClientIds(leadOffers);
                Map<Integer, User> users = userService.getUsers(clientIds);

                Map<Integer, List<UserContactNumber>> contactNumbers = null;
                if (fields.contains("contactNumbers")) {
                    contactNumbers = userService.getUserContactNumbers(clientIds);
                }
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.getLead().setClient(users.get(leadOffer.getLead().getClientId()));
                    if (fields.contains("contactNumbers")) {
                        leadOffer.getLead().getClient()
                                .setContactNumbers(contactNumbers.get(leadOffer.getLead().getClientId()));
                    }
                }
            }

            if (fields.contains("requirements")) {
                List<Integer> leadIds = extractLeadIds(leadOffers);
                Map<Integer, List<LeadRequirement>> requirements = getLeadRequirements(leadIds);
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.getLead().setRequirements(requirements.get(leadOffer.getLeadId()));
                }
            }

            if (fields.contains("offeredListings") || fields.contains("countOfferedListings")) {
                List<Integer> leadOfferIds = extractLeadOfferIds(leadOffers);
                Map<Integer, List<LeadOfferedListing>> leadOfferedListings = getLeadOfferedListing(leadOfferIds);
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setOfferedListings(leadOfferedListings.get(leadOffer.getId()));
                    if (leadOfferedListings.get(leadOffer.getId()) != null) {
                        leadOffer.setCountOfferedListings(leadOffer.getOfferedListings().size());
                    }
                    else {
                        leadOffer.setCountOfferedListings(0);
                    }
                }
            }

            if (fields.contains("countMatchingListings")) {
                List<Integer> leadOfferIds = extractLeadOfferIds(leadOffers);
                List<CountListingObject> leadMatchingListingsCount = leadOfferDao.getMatchingListingCount(leadOfferIds);

                Map<Integer, Long> countMap = new HashMap<>();
                for (CountListingObject countListingObject : leadMatchingListingsCount) {
                    countMap.put(countListingObject.getLeadOfferId(), countListingObject.getCountListings());
                }
                for (LeadOffer leadOffer : leadOffers) {
                    if (countMap.get(leadOffer.getId()) != null) {
                        leadOffer.setCountMatchingListings((int) (long) countMap.get(leadOffer.getId()));
                    }
                    else {
                        leadOffer.setCountMatchingListings(0);
                    }
                }
            }

            if (fields.contains("latestOfferedListings")) {
                List<Integer> leadOfferIds = extractLeadOfferIds(leadOffers);
                Map<Integer, LeadOfferedListing> leadOfferedListings = getLatestLeadOfferedListing(leadOfferIds);
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setLatestOfferedListing(leadOfferedListings.get(leadOffer.getId()));
                }
            }

            if (fields.contains("lastTask")) {
                List<Integer> leadTaskIds = extractLeadLastTaskIds(leadOffers);

                Map<Integer, LeadTask> leadTasks = leadTaskService.getTaskById(leadTaskIds);
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setLastTask(leadTasks.get(leadOffer.getLastTaskId()));
                }
            }

            if (fields.contains("nextTask")) {
                List<Integer> leadTaskIds = extractLeadNextTaskIds(leadOffers);
                Map<Integer, LeadTask> leadTasks = leadTaskService.getTaskById(leadTaskIds);

                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setNextTask(leadTasks.get(leadOffer.getNextTaskId()));
                }
            }

            if (fields.contains("tasks")) {
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setTasks(leadTaskService.getTasksByLeadOfferId(leadOffer.getId()));
                }
            }

            // XXX - Setting lead to null if not passed in fields
            if (!fields.contains("lead")) {
                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.setLead(null);
                }
            }
        }
    }

    private Map<Integer, LeadOfferedListing> getLatestLeadOfferedListing(List<Integer> leadOfferIds) {

        List<Integer> latestOfferedListingIds = leadOfferDao
                .findMaxListingByLeadOfferIdGroupbyLeadOfferId(leadOfferIds);
        List<LeadOfferedListing> latestOfferedListings = leadOfferedListingDao.getListingsById(latestOfferedListingIds);

        Map<Integer, LeadOfferedListing> listingMap = new HashMap<>();

        for (LeadOfferedListing latestOfferedListing : latestOfferedListings) {
            listingMap.put(latestOfferedListing.getLeadOfferId(), latestOfferedListing);
        }

        return listingMap;
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

    private List<Integer> extractLeadLastTaskIds(List<LeadOffer> leadOffers) {
        List<Integer> leadTaskIds = new ArrayList<Integer>();
        for (LeadOffer leadOffer : leadOffers) {
            if (leadOffer.getLastTaskId() != null) {
                leadTaskIds.add(leadOffer.getLastTaskId());
            }
        }
        return leadTaskIds;
    }

    private List<Integer> extractLeadNextTaskIds(List<LeadOffer> leadOffers) {
        List<Integer> leadTaskIds = new ArrayList<Integer>();
        for (LeadOffer leadOffer : leadOffers) {
            if (leadOffer.getNextTaskId() != null) {
                leadTaskIds.add(leadOffer.getNextTaskId());
            }
        }
        return leadTaskIds;
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

    private Map<Integer, List<LeadOfferedListing>> getLeadOfferedListing(List<Integer> leadOfferIds) {

        Map<Integer, List<LeadOfferedListing>> listingMap = new HashMap<>();
        List<LeadOfferedListing> leadOfferListings = leadOfferDao.getLeadOfferedListings(leadOfferIds);

        for (LeadOfferedListing leadOfferedListing : leadOfferListings) {
            if (!listingMap.containsKey(leadOfferedListing.getLeadOfferId())) {
                listingMap.put(leadOfferedListing.getLeadOfferId(), new ArrayList<LeadOfferedListing>());
            }
            listingMap.get(leadOfferedListing.getLeadOfferId()).add(leadOfferedListing);
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
        offer.setAgentId(agent.getUserId());
        offer.setStatusId(LeadOfferStatus.Offered.getLeadOfferStatusId());
        offer.setCycleId(1);
        offer = leadOfferDao.save(offer);
        return offer;
    }

    /**
     * 
     * @param leadOfferId
     * @param integer
     * @return listings for that lead offer id
     */
    public PaginatedResponse<List<Listing>> getOfferedListings(int leadOfferId) {
        List<Listing> listings = new ArrayList<>();
        for (LeadOfferedListing leadOfferListing : leadOfferDao.getLeadOfferedListings(Collections
                .singletonList(leadOfferId))) {
            listings.add(leadOfferListing.getListing());
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

        List<Integer> listingIds = new ArrayList<>();
        List<LeadOfferedListing> leadOfferedListingsGiven = leadOffer.getOfferedListings();

        leadOfferInDB.setLastTask(null);
        leadOfferInDB.setNextTask(null);

        if (leadOfferInDB.getMasterLeadOfferStatus().isClaimedFlag() || leadOfferInDB.getStatusId() == LeadOfferStatus.Offered
                .getLeadOfferStatusId()) {
            if (leadOfferedListingsGiven != null && !leadOfferedListingsGiven.isEmpty()) {
                for (LeadOfferedListing leadOfferedListing : leadOfferedListingsGiven) {
                    listingIds.add(leadOfferedListing.getListingId());
                }
                offerListings(listingIds, leadOfferId, userId);
            }
        }

        if (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered.getLeadOfferStatusId()) {
            if (leadOffer.getStatusId() == LeadOfferStatus.New.getLeadOfferStatusId()) {
                List<LeadOfferedListing> leadOfferedListingList = leadOfferDao.getLeadOfferedListings(Collections
                        .singletonList(leadOfferId));

                if (leadOfferedListingList == null || leadOfferedListingList.isEmpty()) {
                    throw new BadRequestException("To claim add at least one listing");
                }

                leadTaskService.createDefaultLeadTaskForLeadOffer(leadOfferInDB);
                leadOfferInDB.setStatusId(leadOffer.getStatusId());
                leadOfferDao.save(leadOfferInDB);
                leadOfferInDB.setOfferedListings(leadOfferedListingList);
                restrictOtherBrokersFromClaiming(leadOfferId);
                return leadOfferInDB;
            }

            if (leadOffer.getStatusId() == LeadOfferStatus.Declined.getLeadOfferStatusId()) {
                leadOfferInDB.setStatusId(leadOffer.getStatusId());
            }
        }

        leadOfferDao.save(leadOfferInDB);
        return leadOfferInDB;
    }

    private void restrictOtherBrokersFromClaiming(int leadOfferId) {
        LeadOffer leadOffer = leadOfferDao.findById(leadOfferId);
        long leadOfferCount = (long) leadOfferDao.getCountClaimed(leadOffer.getLeadId());

        if (PropertyReader.getRequiredPropertyAsType(PropertyKeys.MARKETPLACE_MAX_BROKER_COUNT_FOR_CLAIM, Long.class)
                .equals(leadOfferCount)) {
            leadOfferDao.expireRestOfTheLeadOffers(leadOffer.getLeadId());
        }
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

    /**
     * utility method for updating lead offer status
     * 
     * @param leadOfferId
     * @param lastTaskId
     * @param nextTaskId
     * @return
     */
    public LeadOffer updateLeadOfferTasks(LeadOffer leadOffer, Integer lastTaskId, Integer nextTaskId) {
        leadOffer.setLastTaskId(lastTaskId);
        leadOffer.setNextTaskId(nextTaskId);
        leadOffer = leadOfferDao.save(leadOffer);
        return leadOffer;
    }

    private PaginatedResponse<List<Listing>> getUnsortedMatchingListings(int leadOfferId) {
        List<Listing> matchingListings = leadOfferDao.getMatchingListings(leadOfferId);
        populateOfferedFlag(leadOfferId, matchingListings);
        return new PaginatedResponse<List<Listing>>(matchingListings, matchingListings.size());
    }

    private void populateOfferedFlag(int leadOfferId, List<Listing> matchingListings) {
        Set<Integer> offeredListingIds = new HashSet<>();
        for (LeadOfferedListing leadOfferListing : leadOfferDao.getLeadOfferedListings(Collections
                .singletonList(leadOfferId))) {
            offeredListingIds.add(leadOfferListing.getListingId());
        }

        for (Listing matchingListing : matchingListings) {
            if (offeredListingIds.contains(matchingListing.getId())) {
                matchingListing.setOffered(true);
            }
        }
    }

    public PaginatedResponse<List<Listing>> getSortedMatchingListings(int leadOfferId) {
        PaginatedResponse<List<Listing>> listings = getUnsortedMatchingListings(leadOfferId);
        List<LeadRequirement> leadRequirements = leadRequirementsService.getRequirements(leadOfferId);
        listings.setResults(sortMatchingListings(listings.getResults(), leadRequirements));
        return listings;
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

        for (Listing listing : listings) {
            int projectId = listing.getProperty().getProjectId();
            if (!listingsByProjectId.containsKey(projectId)) {
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

    public LeadOffer get(int leadOfferId, Integer userId, FIQLSelector selector) {
        LeadOffer leadOffer = leadOfferDao.findById(leadOfferId);
        Set<String> fields = selector.getFieldSet();
        enrichLeadOffers(Collections.singletonList(leadOffer), fields);

        return leadOffer;
    }

    /**
     * This method creates a lead task for email done status and set that task
     * id as previous task id in this lead offer
     * 
     * @param leadOfferId
     * @param userId
     * @param mailDetails2
     * @return
     */
    public LeadOffer updateLeadOfferForEmailTask(int leadOfferId, ActiveUser activeUser, SenderDetail senderDetails) {
        LeadOffer leadOfferInDB = leadOfferDao.findByIdAndAgentId(leadOfferId, activeUser.getUserIdentifier());
        if (leadOfferInDB == null) {
            throw new ResourceNotAvailableException(ResourceType.LEAD_OFFER, ResourceTypeAction.UPDATE);
        }
        /*
         * for email task done status, it should be in claimed status
         */
        if (!leadOfferInDB.getMasterLeadOfferStatus().isClaimedFlag()) {
            throw new BadRequestException("Lead offer " + leadOfferId + " is not in opened status");
        }
        LeadTaskStatus leadTaskStatus = leadTaskStatusDao.getLeadTaskStatusFromTaskNameAndStatusName(
                LeadTaskName.Email.name(),
                LeadTaskStatusName.Done.name());
        if (leadTaskStatus == null) {
            throw new BadRequestException("Email task done status not mapped");
        }

        LeadTask leadTask = new LeadTask();
        leadTask.setLeadOfferId(leadOfferId);
        leadTask.setPerformedAt(new Date());
        leadTask.setTaskStatusId(leadTaskStatus.getId());
        leadTask.setScheduledFor(new Date());
        /*
         * creating task for email status done
         */
        LeadTask createdTask = leadTaskService.createLeadTask(leadTask);
        /*
         * update last task id in lead offer
         */
        leadOfferInDB.setLastTaskId(createdTask.getId());
        leadOfferInDB = leadOfferDao.saveAndFlush(leadOfferInDB);
        /*
         * Send email
         */
        if (senderDetails.getSubject() == null || senderDetails.getSubject().isEmpty()
                || senderDetails.getMessage() == null
                || senderDetails.getMessage().isEmpty()
                || senderDetails.getMailTo() == null
                || senderDetails.getMailTo().isEmpty()) {
            throw new BadRequestException("Invalid mail details");
        }
        MailDetails mailDetails = new MailDetails(new MailBody().setSubject(senderDetails.getSubject()).setBody(
                senderDetails.getMessage())).setMailTo(senderDetails.getMailTo()).setReplyTo(activeUser.getUsername());
        mailSender.sendMailUsingAws(mailDetails);
        return leadOfferInDB;
    }
}
