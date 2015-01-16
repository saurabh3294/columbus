package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.DeclineReason;
import com.proptiger.core.enums.LeadOfferStatus;
import com.proptiger.core.enums.LeadTaskName;
import com.proptiger.core.enums.ListingComparator;
import com.proptiger.core.enums.NotificationType;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.enums.TaskStatus;
import com.proptiger.core.exception.APIServerException;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.internal.dto.mail.MailBody;
import com.proptiger.core.internal.dto.mail.MailDetails;
import com.proptiger.core.model.cms.Company;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.companyuser.CompanyUser;
import com.proptiger.core.model.user.User;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.service.mail.MailSender;
import com.proptiger.core.service.mail.TemplateToHtmlGenerator;
import com.proptiger.core.util.DateUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.internal.dto.SenderDetail;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOffer.CountListingObject;
import com.proptiger.data.model.marketplace.LeadOfferedListing;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingDao;
import com.proptiger.data.repo.marketplace.MasterLeadOfferStatusDao;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.service.user.CompanyUserServiceHelper;
import com.proptiger.data.service.user.UserServiceHelper;

/**
 * 
 * @author azi
 * 
 */

@Service
public class LeadOfferService {
    @Autowired
    private CompanyUserServiceHelper companyUserServiceHelper;

    @Autowired
    private LeadOfferDao                 leadOfferDao;

    @Autowired
    private LeadDao                      leadDao;

    @Autowired
    private LeadRequirementsService      leadRequirementsService;

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

    private LeadService                  leadService;

    @Autowired
    private ApplicationContext           applicationContext;

    @Autowired
    private DeclineReasonService         declineReasonService;

    private static Logger                logger = LoggerFactory.getLogger(LeadOfferService.class);
    
    @Autowired
    private UserServiceHelper userServiceHelper;

    private LeadService getLeadService() {
        if (leadService == null) {
            leadService = applicationContext.getBean(LeadService.class);
        }
        return leadService;
    }

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

        Set<String> fields = selector.getFieldSet();

        PaginatedResponse<List<LeadOffer>> paginatedResponse = leadOfferDao.getLeadOffers(
                agentId,
                statusIds,
                dueDate,
                selector);

        enrichLeadOffers(paginatedResponse.getResults(), fields, agentId);

        return paginatedResponse;
    }

    private void enrichLeadOffers(List<LeadOffer> leadOffers, Set<String> fields, Integer userId) {
        if (fields != null && leadOffers != null && !leadOffers.isEmpty()) {
            if (fields.contains("client")) {
                Set<Integer> clientIds = extractClientIds(leadOffers);
                Map<Integer, User> users = userServiceHelper.getUserWithCompleteDetailsByUserIds_CallerNonLogin(clientIds);

                for (LeadOffer leadOffer : leadOffers) {
                    leadOffer.getLead().setClient(users.get(leadOffer.getLead().getClientId()));
                    if (fields.contains("contactNumbers")) {
                        leadOffer.getLead().getClient()
                                .setContactNumbers(users.get(leadOffer.getLead().getClientId()).getContactNumbers());
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
            List<LeadOfferedListing> latestOfferedListings = leadOfferedListingDao.getByIdIn(latestOfferedListingIds);

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

    private Set<Integer> extractClientIds(List<LeadOffer> leadOffers) {
        Set<Integer> clientIds = new HashSet<Integer>();
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
        List<LeadOfferedListing> leadOfferListings = leadOfferedListingDao.getByLeadOfferIdAndAgentId(
                leadOfferIds,
                userId);

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
        List<CompanyUser> agents = companyUserServiceHelper.getCompanyUsersInCompany(brokerCompany.getId());
        Integer countLeadOfferInDB = (int) (long) leadOfferDao.getCountByLeadIdAndPhaseId(lead.getId(), phaseId);
        LeadOffer leadOffer = null;
        LeadOffer leadOfferInDB = null;
        if (!agents.isEmpty()) {
            leadOfferInDB = leadOfferDao.findByLeadIdAndAgentId(lead.getId(), agents.get(0).getUserId());
        }
        if (!agents.isEmpty() && leadOfferInDB == null) {
            leadOffer = createLeadOffer(lead, agents.get(0), cycleId, countLeadOfferInDB, phaseId);
        }
        return leadOffer;
    }

    public LeadOffer createLeadOffer(Lead lead, CompanyUser agent, int cycleId, int countLeadOfferInDB, Integer phaseId) {
        LeadOffer offer = new LeadOffer();
        offer.setLeadId(lead.getId());
        offer.setAgentId(agent.getUserId());
        offer.setStatusId(LeadOfferStatus.Offered.getId());

        if (phaseId == null || phaseId == 0) {
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
        for (LeadOfferedListing leadOfferListing : leadOfferedListingDao.getByLeadOfferIdAndAgentId(
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
        LeadOffer leadOfferInDB = leadOfferDao.getById(leadOfferId);

        if (leadOfferInDB == null || leadOfferInDB.getAgentId() != userId) {
            throw new BadRequestException("Invalid lead offer");
        }

        // Offer listings
        List<Integer> newListingIds = offerListings(leadOffer, leadOfferInDB, userId);

        // Claim a lead
        if (leadOfferInDB.getStatusId() == LeadOfferStatus.Offered.getId()) {
            if (leadOffer.getStatusId() == LeadOfferStatus.New.getId()) {

                long countLeadOffersOnThisAgentInNewStatus = leadOfferDao.getCountByAgentIdAndStatusId(
                        userId,
                        LeadOfferStatus.New.getId());

                if (countLeadOffersOnThisAgentInNewStatus < PropertyReader.getRequiredPropertyAsType(
                        PropertyKeys.MARKETPLACE_MAX_LEADS_LIMIT_FOR_COMPANY_NEW_STATUS,
                        Long.class)) {
                    claimLeadOffer(leadOffer, leadOfferInDB, newListingIds, userId);
                    Notification notification = notificationService.findByUserIdAndNotificationId(
                            userId,
                            NotificationType.MaxLeadCountForBrokerReached.getId(),
                            0);
                    if (notification != null) {
                        notificationService.deleteNotification(
                                userId,
                                NotificationType.MaxLeadCountForBrokerReached.getId(),
                                0);
                        notificationService
                                .deleteNotification(
                                        PropertyReader
                                                .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_RELATIONSHIP_MANAGER_USER_ID),
                                        NotificationType.MaxLeadCountForBrokerReached.getId(),
                                        userId);
                    }

                    if (countLeadOffersOnThisAgentInNewStatus + 1 == PropertyReader.getRequiredPropertyAsType(
                            PropertyKeys.MARKETPLACE_MAX_LEADS_LIMIT_FOR_COMPANY_NEW_STATUS,
                            Long.class)) {
                        Notification notificationLeadLimit = notificationService.findByUserIdAndNotificationId(
                                userId,
                                NotificationType.MaxLeadCountForBrokerReached.getId(),
                                0);
                        if (notificationLeadLimit == null) {
                            notificationService.createNotification(
                                    userId,
                                    NotificationType.MaxLeadCountForBrokerReached.getId(),
                                    0,
                                    null);
                            notificationService
                                    .createNotification(
                                            PropertyReader
                                                    .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_RELATIONSHIP_MANAGER_USER_ID),
                                            8,
                                            userId,
                                            null);
                        }
                    }
                }
                else {
                    throw new APIServerException(
                            "Claim Lead suspended ,Please update your existing New leads to claim new leads ");
                }

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

                if (leadOffer.getDeclineReasonId() == null || declineReasonService.getReasonById(leadOffer
                        .getDeclineReasonId()) == null) {
                    throw new BadRequestException("please provide valid reason for declining");
                }
                else if (leadOffer.getDeclineReasonId() == DeclineReason.Other.getDeclineReasonId()) {
                    if (leadOffer.getOtherReason() == null || leadOffer.getOtherReason() == "") {
                        throw new BadRequestException("please provide valid reason for declining");
                    }
                    else {
                        leadOfferInDB.setOtherReason(leadOffer.getOtherReason());
                        leadOfferInDB.setDeclineReasonId(leadOffer.getDeclineReasonId());
                    }
                }
                else {
                    leadOfferInDB.setDeclineReasonId(leadOffer.getDeclineReasonId());
                }

                notificationService.removeNotification(leadOfferInDB);
                leadOfferInDB.setStatusId(leadOffer.getStatusId());
                leadOfferDao.save(leadOfferInDB);
                restrictOtherBrokersFromClaiming(leadOfferInDB.getId());
                return leadOfferInDB;
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
        List<LeadOfferedListing> leadOfferedListingList = leadOfferedListingDao.getByLeadOfferIdAndAgentId(
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
        logger.debug("Sending email from inside claim lead");
        sendMailToClient(leadOfferInDB, templatePath, heading, newListingIds, userId, "Proptiger.com");
    }

    @Transactional
    public void manageLeadOfferedNotificationDeletionForLead(int leadId) {
        Lead lead = leadDao.getLock(leadId);
        List<LeadOffer> offers = leadOfferDao.getByLeadId(leadId);

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

                if (newListingIds != null && !newListingIds.isEmpty()
                        && leadOfferInDB.getStatusId() != LeadOfferStatus.Offered.getId()) {
                    logger.debug("Sending email from inside offer listings");
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
            
            leadOfferInDB.setAgent(userServiceHelper.getUserById_CallerNonLogin(leadOfferInDB.getAgentId()));
            map.put("leadOffer", leadOfferInDB);
            map.put("listingObjectWithAmenities", listingMap);

            if (username == null) {
                username = leadOfferInDB.getAgent().getFullName();
            }

            String template = templateToHtmlGenerator.generateHtmlFromTemplate(map, templatePath);
            MailDetails mailDetails = new MailDetails(new MailBody().setSubject(heading).setBody(template))
                    .setMailTo(leadOfferInDB.getLead().getClient().getEmail())
                    .setMailCC(leadOfferInDB.getAgent().getEmail()).setReplyTo(leadOfferInDB.getAgent().getEmail())
                    .setFrom(username + "<" + propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_NOREPLY) + ">")
                    .setMailBCC(propertyReader.getRequiredProperty(PropertyKeys.MARKETPLACE_BCC_EMAIL));
            mailSender.sendMailUsingAws(mailDetails);
        }
    }

    private void restrictOtherBrokersFromClaiming(int leadOfferId) {
        LeadOffer leadOfferInDB = leadOfferDao.getById(leadOfferId);
        Integer maxPhaseId = leadOfferDao.getMaxPhaseIdByLeadId(leadOfferInDB.getLeadId());
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

        if (PropertyReader
                .getRequiredPropertyAsType(PropertyKeys.MARKETPLACE_MAX_BROKER_COUNT_FOR_CLAIM, Integer.class).equals(
                        leadOfferCount)) {
            leadOfferDao.updateStatusByLeadIdInAndStatus(
                    Arrays.asList(leadOfferInDB.getLeadId()),
                    LeadOfferStatus.Offered.getId(),
                    LeadOfferStatus.Expired.getId());
        }
        else {

            if (declinedLeadOfferCountInCycle + leadOfferCountInCycle == allCountInCycle) {
                getLeadService();
                leadService.manageLeadAuctionWithBeforeCycleDeclined(leadOfferInDB.getLeadId());
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

        LeadOffer leadOfferInDB = leadOfferDao.getById(leadOfferId);
        if (leadOfferInDB.getAgentId() != userId) {
            throw new BadRequestException("you can only view listings offered by you for lead offers assigned to you");
        }
        List<Listing> matchingListings = leadOfferDao.getMatchingListings(leadOfferId, userId);
        populateOfferedFlag(leadOfferId, matchingListings, userId);
        return new PaginatedResponse<List<Listing>>(matchingListings, matchingListings.size());
    }

    private void populateOfferedFlag(int leadOfferId, List<Listing> matchingListings, Integer userId) {
        Set<Integer> offeredListingIds = new HashSet<>();
        for (LeadOfferedListing leadOfferListing : leadOfferedListingDao.getByLeadOfferIdAndAgentId(
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
        List<Listing> sortedListProject = new ArrayList<>();
        List<Listing> sortedListLocality = new ArrayList<>();
        List<Listing> sortedListRemaining = new ArrayList<>();

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
                sortedListProject.addAll(listingsByProjectId.get(projectId));
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
                sortedListLocality.addAll(listingsByLocalityId.get(localityId));
                listingsByLocalityId.remove(localityId);
            }
        }

        List<ListingComparator> compratorList = new ArrayList<ListingComparator>();
        compratorList.add(ListingComparator.NAME_SORT);
        compratorList.add(ListingComparator.ID_SORT);
        compratorList.add(ListingComparator.PRICE_SORT);

        Collections
                .sort(sortedListProject, ListingComparator.ascending(ListingComparator.getComparator(compratorList)));
        Collections.sort(
                sortedListLocality,
                ListingComparator.ascending(ListingComparator.getComparator(compratorList)));

        sortedList.addAll(sortedListProject);
        sortedList.addAll(sortedListLocality);

        for (List<Listing> remainingListings : listingsByLocalityId.values()) {
            sortedListRemaining.addAll(remainingListings);
        }
        Collections.sort(
                sortedListRemaining,
                ListingComparator.ascending(ListingComparator.getComparator(compratorList)));
        sortedList.addAll(sortedListRemaining);

        return sortedList;
    }

    public LeadOffer get(int leadOfferId, Integer userId, FIQLSelector selector) {
        LeadOffer leadOffer = leadOfferDao.getById(leadOfferId);
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
        LeadOffer leadOfferInDB = leadOfferDao.getById(leadOfferId);
        if (leadOfferInDB == null || leadOfferInDB.getAgentId() != activeUser.getUserIdentifier()) {
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

        String username = userServiceHelper.getLoggedInUserObj().getFullName();

        MailDetails mailDetails = new MailDetails(new MailBody().setSubject(senderDetails.getSubject()).setBody(
                senderDetails.getMessage())).setMailTo(senderDetails.getMailTo()).setMailCC(activeUser.getUsername())
                .setReplyTo(activeUser.getUsername())
                .setFrom(username + "<" + propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_NOREPLY) + ">")
                .setMailBCC(propertyReader.getRequiredProperty(PropertyKeys.MARKETPLACE_BCC_EMAIL));
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
        return leadOfferDao.getMaxCycleIdByLeadIdAndPhaseId(id, phaseId);
    }
}
