package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.enums.LeadTaskName;
import com.proptiger.data.enums.NotificationType;
import com.proptiger.data.enums.TaskStatus;
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
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingDao;
import com.proptiger.data.repo.marketplace.MasterLeadOfferStatusDao;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.service.companyuser.CompanyService;
import com.proptiger.data.service.cron.CronService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 * @author azi
 * 
 */

@Service
public class LeadOfferService {
    @Autowired
    private CompanyService               companyService;

    @Autowired
    private LeadOfferDao                 leadOfferDao;

    @Autowired
    private LeadDao                      leadDao;

    @Autowired
    private LeadRequirementsService      leadRequirementsService;

    @Autowired
    private UserService                  userService;

    @Autowired
    private LeadOfferedListingDao        leadOfferedListingDao;

    @Autowired
    private LeadTaskService              leadTaskService;

    @Autowired
    private ListingService               listingService;

    @Autowired
    private LeadTaskStatusDao            leadTaskStatusDao;

    @Autowired
    private MasterLeadOfferStatusDao     leadOfferStatusDao;

    @Autowired
    private MailSender                   mailSender;

    @Autowired
    private NotificationGeneratedService generatedService;

    @Value("${marketplace.template.base.path}")
    private String                       marketplaceTemplateBasePath;

    @Value("${marketplace.template.claim}")
    private String                       claimTemplate;

    @Value("${marketplace.template.offer}")
    private String                       offerTemplate;

    @Autowired
    private TemplateToHtmlGenerator      templateToHtmlGenerator;

    @Autowired
    private NotificationService          notificationService;

    @Autowired
    private PropertyReader               propertyReader;

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
        for (Listing listing : getUnsortedMatchingListings(leadOfferId, userId).getResults()) {
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
    public PaginatedResponse<List<LeadOffer>> getLeadOffers(
            int agentId,
            FIQLSelector selector,
            List<Integer> statusIds,
            String dueDate) {
        PaginatedResponse<List<LeadOffer>> paginatedResponse = leadOfferDao.getLeadOffers(
                agentId,
                statusIds,
                dueDate,
                selector);

        Set<String> fields = selector.getFieldSet();
        enrichLeadOffers(paginatedResponse.getResults(), fields, agentId);

        return paginatedResponse;
    }

    private void enrichLeadOffers(List<LeadOffer> leadOffers, Set<String> fields, Integer userId) {
        if (fields != null && leadOffers != null && !leadOffers.isEmpty()) {
            if (fields.contains("client")) {
                List<Integer> clientIds = extractClientIds(leadOffers);
                Map<Integer, User> users = userService.getUsers(clientIds);

                Map<Integer, Set<UserContactNumber>> contactNumbers = null;
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
                Map<Integer, List<LeadOfferedListing>> leadOfferedListings = getLeadOfferedListing(leadOfferIds, userId);
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
        Map<Integer, LeadOfferedListing> listingMap = new HashMap<>();

        List<Integer> latestOfferedListingIds = leadOfferDao
                .findMaxListingByLeadOfferIdGroupbyLeadOfferId(leadOfferIds);

        if (latestOfferedListingIds != null && !latestOfferedListingIds.isEmpty()) {
            List<LeadOfferedListing> latestOfferedListings = leadOfferedListingDao
                    .getListingsById(latestOfferedListingIds);

            for (LeadOfferedListing latestOfferedListing : latestOfferedListings) {
                listingMap.put(latestOfferedListing.getLeadOfferId(), latestOfferedListing);
            }
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

    private Map<Integer, List<LeadOfferedListing>> getLeadOfferedListing(List<Integer> leadOfferIds, Integer userId) {

        Map<Integer, List<LeadOfferedListing>> listingMap = new HashMap<>();
        List<LeadOfferedListing> leadOfferListings = leadOfferDao.getLeadOfferedListings(leadOfferIds, userId);

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

    public LeadOffer offerLeadToBroker(Lead lead, Company brokerCompany, int cycleId, Integer phaseId) {
        List<CompanyUser> agents = companyService.getCompanyUsersForCompanies(brokerCompany);
        Integer countLeadOfferInDB = (int) (long) leadOfferDao.findByLeadIdAndPhaseId(lead.getId(), phaseId);
        LeadOffer leadOffer = null;
        if (!agents.isEmpty()) {
            leadOffer = createLeadOffer(lead, agents.get(0), cycleId, countLeadOfferInDB, phaseId);
        }
        return leadOffer;
    }

    public LeadOffer createLeadOffer(Lead lead, CompanyUser agent, int cycleId, int countLeadOfferInDB, Integer phaseId) {
        LeadOffer offer = new LeadOffer();
        offer.setLeadId(lead.getId());
        offer.setAgentId(agent.getUserId());
        offer.setStatusId(LeadOfferStatus.Offered.getId());

        if (phaseId == null) {
            phaseId = 1;
        }

        if (countLeadOfferInDB >= PropertyReader.getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_MAX_OFFERS_IN_PHASE)) {
            phaseId = phaseId + 1;
        }

        offer.setPhaseId(phaseId);
        offer.setCycleId(cycleId);
        offer = leadOfferDao.save(offer);
        return offer;
    }

    /**
     * 
     * @param leadOfferId
     * @param integer
     * @return listings for that lead offer id
     */
    public PaginatedResponse<List<Listing>> getOfferedListings(int leadOfferId, Integer userId) {
        List<Listing> listings = new ArrayList<>();
        for (LeadOfferedListing leadOfferListing : leadOfferDao.getLeadOfferedListings(
                Collections.singletonList(leadOfferId),
                userId)) {
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
        LeadOffer leadOfferInDB = leadOfferDao.findByIdAndAgentIdAndFetchLead(leadOfferId, userId);

        if (leadOfferInDB == null) {
            throw new BadRequestException("Invalid lead offer");
        }

        // Offer listings
        List<Integer> newListingIds = offerListings(leadOffer, leadOfferInDB, userId);

        // Claim a lead
        if (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered.getId()) {
            if (leadOffer.getStatusId() == LeadOfferStatus.New.getId()) {
                claimLeadOffer(leadOffer, leadOfferInDB, newListingIds, userId);
                return leadOfferInDB;
            }
        }

        // Trying to claim a lead not in offered state
        if (!leadOfferInDB.getMasterLeadOfferStatus().isClaimed() && leadOffer.getStatusId() == LeadOfferStatus.New
                .getId()) {
            throw new BadRequestException("Sorry! The lead has already been claimed by another agent.");
        }

        // Declining a lead from Offered or Expired state
        if (leadOffer.getStatusId() == LeadOfferStatus.Declined.getId()) {
            if (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered.getId() || leadOfferInDB.getStatusId() == LeadOfferStatus.Expired
                    .getId()) {
                notificationService.removeNotification(leadOfferInDB);
                leadOfferInDB.setStatusId(leadOffer.getStatusId());
            }
        }

        leadOfferInDB.setLead(null);
        leadOfferDao.save(leadOfferInDB);
        return leadOfferInDB;
    }

    /**
     * @param leadOffer
     * @param leadOfferId
     * @param leadOfferInDB
     */
    private void claimLeadOffer(
            LeadOffer leadOffer,
            LeadOffer leadOfferInDB,
            List<Integer> newListingIds,
            Integer userId) {
        List<LeadOfferedListing> leadOfferedListingList = leadOfferDao.getLeadOfferedListings(
                Collections.singletonList(leadOfferInDB.getId()),
                userId);
        if (leadOfferedListingList == null || leadOfferedListingList.isEmpty()) {
            throw new BadRequestException("To claim add at least one listing");
        }

        leadTaskService.createDefaultLeadTaskForLeadOffer(leadOfferInDB);
        leadOfferInDB.setStatusId(leadOffer.getStatusId());
        leadOfferDao.save(leadOfferInDB);
        leadOfferInDB.setOfferedListings(leadOfferedListingList);
        restrictOtherBrokersFromClaiming(leadOfferInDB.getId());
        manageLeadOfferedNotificationDeletionForLead(leadOfferInDB.getLeadId());
        String heading = "Matching Property suggested by our trusted broker";
        String templatePath = marketplaceTemplateBasePath + claimTemplate;
        sendMailToClient(leadOfferInDB, templatePath, heading, newListingIds, userId, "Proptiger.com");
    }

    @Transactional
    public void manageLeadOfferedNotificationDeletionForLead(int leadId) {
        Lead lead = leadDao.getLock(leadId);
        List<LeadOffer> offers = leadOfferDao.getLeadOffers(leadId);

        Date endDate = notificationService.getNoBrokerClaimedCutoffTime();
        Date startDate = new Date(
                endDate.getTime() - PropertyReader.getRequiredPropertyAsInt((PropertyKeys.MARKETPLACE_CRON_BUFFER))
                        * 1000);

        Date maxOfferDate = new Date(0);
        boolean claimed = false;
        for (LeadOffer offer : offers) {
            claimed = claimed || offer.getMasterLeadOfferStatus().isClaimed();
            maxOfferDate = DateUtil.max(maxOfferDate, offer.getCreatedAt());
        }

        if (claimed || (maxOfferDate.after(startDate) && maxOfferDate.before(endDate))) {
            if (!claimed) {
                this.expireLeadOffersInOfferedStatus(offers);
                notificationService
                        .sendEmail(
                                notificationService.getRelationshipManagerUserId(),
                                NotificationType.NoBrokerClaimed.getEmailSubject(),
                                "Lead ID: " + leadId
                                        + " of resale marketplace was not claimed by any broker. Marking all offers as expired.");
                notificationService.createNotification(
                        notificationService.getRelationshipManagerUserId(),
                        NotificationType.NoBrokerClaimed.getId(),
                        leadId,
                        null);
                // skipping interested in primary check
                // if
                // (lead.getTransactionType().equals(ListingCategory.PrimaryAndResale.toString()))
                // {
                notificationService.moveToPrimary(leadId);
                // }
            }
            notificationService.deleteLeadOfferNotificationForLead(offers);
        }
    }

    /**
     * @param leadOffer
     * @param leadOfferId
     * @param userId
     * @param leadOfferInDB
     */
    private List<Integer> offerListings(LeadOffer leadOffer, LeadOffer leadOfferInDB, Integer userId) {
        List<Integer> listingIds = new ArrayList<>();
        List<LeadOfferedListing> leadOfferedListingsGiven = leadOffer.getOfferedListings();

        leadOfferInDB.setLastTask(null);
        leadOfferInDB.setNextTask(null);

        // Code that offers listings
        if (leadOfferInDB.getMasterLeadOfferStatus().isClaimed() || (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered
                .getId() && leadOffer.getStatusId() == LeadOfferStatus.New.getId())) // Trying
                                                                                     // to
                                                                                     // claim
                                                                                     // case
        {
            int maxOfferCountWhileClaiming = PropertyReader.getRequiredPropertyAsType(
                    PropertyKeys.MARKETPLACE_MAX_PROPERTY_COUNT_WHILE_CLAIMING,
                    Long.class).intValue();
            if (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered.getId() && leadOfferedListingsGiven != null
                    && leadOfferedListingsGiven.size() > maxOfferCountWhileClaiming) {
                throw new BadRequestException("Currently you can offer only " + maxOfferCountWhileClaiming
                        + " properties to the client. You may offer more later");
            }

            if (leadOfferedListingsGiven != null && !leadOfferedListingsGiven.isEmpty()) {
                for (LeadOfferedListing leadOfferedListing : leadOfferedListingsGiven) {
                    listingIds.add(leadOfferedListing.getListingId());
                }

                List<Integer> newListingIds = offerListings(
                        listingIds,
                        leadOfferInDB.getId(),
                        leadOfferInDB.getAgentId());
                String heading = "More properties matching your requirement";
                String templatePath = marketplaceTemplateBasePath + offerTemplate;

                if (leadOfferInDB.getStatusId() != LeadOfferStatus.Offered.getId()) {
                    sendMailToClient(leadOfferInDB, templatePath, heading, newListingIds, userId, null);
                }
                return newListingIds;
            }
        }
        return null;
    }

    private void sendMailToClient(
            LeadOffer leadOfferInDB,
            String templatePath,
            String heading,
            List<Integer> newListingIds,
            Integer userId,
            String username) {

        if (newListingIds != null) {
            Set<String> fields = new HashSet<>();
            fields.add("lead");
            fields.add("offeredListings");
            fields.add("client");
            fields.add("contactNumbers");
            fields.add("requirements");
            enrichLeadOffers(Collections.singletonList(leadOfferInDB), fields, userId);
            Map<String, Object> map = new HashMap<>();

            for (LeadOfferedListing leadOfferedListing : leadOfferInDB.getOfferedListings()) {
                if (!newListingIds.contains(leadOfferedListing.getListingId())) {
                    leadOfferedListing.setListing(null);
                }
            }
            FIQLSelector fiqlSelector = new FIQLSelector();
            fiqlSelector.setFields("id,listingAmenities,amenity,amenityDisplayName,amenityMaster,amenityId,jsonDump");
            List<Listing> listings = listingService.getListings(leadOfferInDB.getAgentId(), fiqlSelector).getResults();
            Map<Integer, Listing> listingMap = new HashMap<>();
            for (Listing listing : listings) {
                if (newListingIds.contains(listing.getId())) {
                    listingMap.put(listing.getId(), listing);
                }
            }

            leadOfferInDB.setAgent(userService.getUserWithContactNumberById(leadOfferInDB.getAgentId()));
            map.put("leadOffer", leadOfferInDB);
            map.put("listingObjectWithAmenities", listingMap);

            if (username == null) {
                username = leadOfferInDB.getAgent().getFullName();
            }

            String template = templateToHtmlGenerator.generateHtmlFromTemplate(map, templatePath);
            MailDetails mailDetails = new MailDetails(new MailBody().setSubject(heading).setBody(template))
                    .setMailTo(leadOfferInDB.getLead().getClient().getEmail())
                    .setMailCC(leadOfferInDB.getAgent().getEmail()).setReplyTo(leadOfferInDB.getAgent().getEmail())
                    .setFrom(username + "<" + propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_NOREPLY) + ">");
            mailSender.sendMailUsingAws(mailDetails);
        }
    }

    private void restrictOtherBrokersFromClaiming(int leadOfferId) {
        LeadOffer leadOfferInDB = leadOfferDao.findById(leadOfferId);
        Integer maxPhaseId = leadOfferDao.getMaxPhaseId(leadOfferInDB.getLeadId());
        List<LeadOffer> allLeadOffers = leadOfferDao.findByLeadId(leadOfferInDB.getLeadId());

        Integer leadOfferCount = 0;
        Integer declinedLeadOfferCountInCycle = 0;
        Integer maxCycleId = 0;
        Integer leadOfferCountInCycle = 0;
        Integer allCountInCycle = 0;
        for (LeadOffer leadOffer : allLeadOffers) {
            if (leadOffer.getMasterLeadOfferStatus().isClaimed() == true && leadOffer.getPhaseId() == maxPhaseId) {
                leadOfferCount++;
            }
            if (leadOffer.getCycleId() > maxCycleId && leadOffer.getPhaseId() == maxPhaseId) {
                maxCycleId = leadOffer.getCycleId();
            }
        }

        for (LeadOffer leadOffer : allLeadOffers) {
            if (leadOffer.getMasterLeadOfferStatus().isClaimed() == true && leadOffer.getPhaseId() == maxPhaseId
                    && leadOffer.getCycleId() == maxCycleId) {
                leadOfferCountInCycle++;
            }
            if (leadOffer.getStatusId() == LeadOfferStatus.Declined.getId() && leadOffer.getPhaseId() == maxPhaseId
                    && leadOffer.getCycleId() == maxCycleId) {
                declinedLeadOfferCountInCycle++;
            }
            if (leadOffer.getCycleId() == maxCycleId) {
                allCountInCycle++;
            }
        }

        if (PropertyReader.getRequiredPropertyAsType(PropertyKeys.MARKETPLACE_MAX_BROKER_COUNT_FOR_CLAIM, Long.class)
                .equals(leadOfferCount)) {
            leadOfferDao.expireRestOfTheLeadOffers(leadOfferInDB.getLeadId());
        }
        else {
            if (declinedLeadOfferCountInCycle + leadOfferCountInCycle == allCountInCycle) {
                LeadService leadService = new LeadService();
                leadService.manageLeadAuctionWithBeforeCycle(leadOfferInDB.getLeadId());
            }
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
        if (leadOffer.getMasterLeadOfferStatus().getLevel() < leadOfferStatusDao.findOne(statusId).getLevel()) {
            leadOffer.setStatusId(statusId);
            leadOffer = leadOfferDao.save(leadOffer);
        }
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

    private PaginatedResponse<List<Listing>> getUnsortedMatchingListings(int leadOfferId, Integer userId) {
        List<Listing> matchingListings = leadOfferDao.getMatchingListings(leadOfferId);
        populateOfferedFlag(leadOfferId, matchingListings, userId);
        return new PaginatedResponse<List<Listing>>(matchingListings, matchingListings.size());
    }

    private void populateOfferedFlag(int leadOfferId, List<Listing> matchingListings, Integer userId) {
        Set<Integer> offeredListingIds = new HashSet<>();
        for (LeadOfferedListing leadOfferListing : leadOfferDao.getLeadOfferedListings(
                Collections.singletonList(leadOfferId),
                userId)) {
            offeredListingIds.add(leadOfferListing.getListingId());
        }

        for (Listing matchingListing : matchingListings) {
            if (offeredListingIds.contains(matchingListing.getId())) {
                matchingListing.setOffered(true);
            }
        }
    }

    public PaginatedResponse<List<Listing>> getSortedMatchingListings(int leadOfferId, Integer userId) {
        PaginatedResponse<List<Listing>> listings = getUnsortedMatchingListings(leadOfferId, userId);
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
        Map<Integer, List<Listing>> listingsByLocalityId = new HashMap<>();
        List<Listing> remainingAfterProjectSort = new ArrayList<>();

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
            remainingAfterProjectSort.addAll(remainingListings);
        }

        for (Listing listing : remainingAfterProjectSort) {
            int localityId = listing.getProperty().getProject().getLocalityId();
            if (!listingsByLocalityId.containsKey(localityId)) {
                listingsByLocalityId.put(localityId, new ArrayList<Listing>());
            }
            listingsByLocalityId.get(localityId).add(listing);
        }

        for (LeadRequirement leadRequirement : leadRequirements) {
            Integer localityId = null;

            if (leadRequirement.getProject() != null && leadRequirement.getProjectId() != null) {
                localityId = leadRequirement.getProject().getLocalityId();
            }
            else {
                localityId = leadRequirement.getLocalityId();
            }

            if (listingsByLocalityId.containsKey(localityId)) {
                sortedList.addAll(listingsByLocalityId.get(localityId));
                listingsByLocalityId.remove(localityId);
            }
        }

        for (List<Listing> remainingListings : listingsByLocalityId.values()) {
            sortedList.addAll(remainingListings);
        }
        return sortedList;
    }

    public LeadOffer get(int leadOfferId, Integer userId, FIQLSelector selector) {
        LeadOffer leadOffer = leadOfferDao.findById(leadOfferId);
        Set<String> fields = selector.getFieldSet();
        enrichLeadOffers(Collections.singletonList(leadOffer), fields, userId);

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
    @Transactional
    public LeadOffer updateLeadOfferForEmailTask(int leadOfferId, ActiveUser activeUser, SenderDetail senderDetails) {
        LeadOffer leadOfferInDB = leadOfferDao.findByIdAndAgentId(leadOfferId, activeUser.getUserIdentifier());
        if (leadOfferInDB == null) {
            throw new ResourceNotAvailableException(ResourceType.LEAD_OFFER, ResourceTypeAction.UPDATE);
        }
        /*
         * for email task done status, it should be in claimed status
         */
        if (!leadOfferInDB.getMasterLeadOfferStatus().isClaimed()) {
            throw new BadRequestException("Lead offer " + leadOfferId + " is not in opened status");
        }
        LeadTaskStatus leadTaskStatus = leadTaskStatusDao.getLeadTaskStatusFromTaskNameAndStatusName(
                LeadTaskName.Email.name(),
                TaskStatus.Done);
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

        leadTask.setNotes(senderDetails.getSubject());
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

        String username = userService.getUserById(activeUser.getUserIdentifier()).getFullName();

        MailDetails mailDetails = new MailDetails(new MailBody().setSubject(senderDetails.getSubject()).setBody(
                senderDetails.getMessage())).setMailTo(senderDetails.getMailTo()).setMailCC(activeUser.getUsername())
                .setReplyTo(activeUser.getUsername())
                .setFrom(username + "<" + propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_NOREPLY) + ">");
        mailSender.sendMailUsingAws(mailDetails);
        return leadOfferInDB;
    }

    public List<LeadOffer> expireLeadOffersInOfferedStatus(List<LeadOffer> leadOffers) {
        for (LeadOffer leadOffer : leadOffers) {
            if (leadOffer.getMasterLeadOfferStatus().getStatus().equals(LeadOfferStatus.Offered.toString())) {
                leadOffer.setStatusId(LeadOfferStatus.Expired.getId());
                leadOfferDao.save(leadOffer);
            }
        }
        return leadOffers;
    }

    public Integer getMaxCycleIdAndPhaseId(int id, int phaseId) {
        return leadOfferDao.getMaxCycleIdAndPhaseId(id, phaseId);
    }
}
