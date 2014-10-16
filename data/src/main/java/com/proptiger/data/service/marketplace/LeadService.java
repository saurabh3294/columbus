package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.enums.NotificationType;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.service.CityService;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.companyuser.CompanyService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ProAPIException;

/**
 * @author Anubhav
 * @param
 * 
 */
@Service
public class LeadService {
    @Autowired
    private UserService             userService;

    @Autowired
    private LeadDao                 leadDao;

    @Autowired
    private LeadRequirementsService leadRequirementsService;

    @Autowired
    private LeadOfferDao            leadOfferDao;

    @Autowired
    private LeadOfferService        leadOfferService;

    @Autowired
    private ProjectService          projectService;

    @Autowired
    private CompanyService          companyService;

    @Autowired
    private LocalityService         localityService;

    @Autowired
    private PropertyReader          propertyReader;

    private static Logger           logger = LoggerFactory.getLogger(LeadService.class);

    @Autowired
    private NotificationService     notificationService;

    @Autowired
    private CityService             cityService;

    public void manageLeadAuctionWithBeforeCycleForRequestBrokers(int leadId) {
        Integer maxPhaseIdForRequestMoreBrokers = leadOfferDao.getMaxPhaseId(leadId);

        if (leadOfferDao.findByLeadIdAndPhaseId(leadId, maxPhaseIdForRequestMoreBrokers).equals(
                PropertyReader.getRequiredPropertyAsType(PropertyKeys.MARKETPLACE_MAX_OFFERS_IN_PHASE, Long.class))) {
            leadOfferDao.updateLeadOffers(Collections.singletonList(leadId));
            Map<Integer, Integer> phaseIdMapLeadId = new HashMap<Integer, Integer>();
            phaseIdMapLeadId.put(leadId, maxPhaseIdForRequestMoreBrokers + 1);
            manageLeadAuctionWithCycle(leadId, phaseIdMapLeadId, maxPhaseIdForRequestMoreBrokers + 1,1);

        }
    }

    public void manageLeadAuctionWithBeforeCycle(int leadId) {
        Map<Integer, Integer> phaseIdMapLeadId = new HashMap<Integer, Integer>();
        phaseIdMapLeadId.put(leadId, 0);
        manageLeadAuctionWithCycle(leadId, phaseIdMapLeadId, 0, 0);
    }

    public void manageLeadAuctionWithCycle(
            int leadId,
            Map<Integer, Integer> maxPhaseIdMapLeadId,
            Integer maxPhaseIdForRequestMoreBrokers,int flagRequest) {

        Lead lead = leadDao.getLock(leadId);
        lead.setRequestBrokerPhaseId(maxPhaseIdForRequestMoreBrokers);
        List<Company> brokerCompanies = getBrokersForLeadWithCycleExcludingAlreadyOffered(lead);

        boolean isAssigned = false;
        if (brokerCompanies.isEmpty()) {
            // XXX No broker found alert in future
        }
        else {
            int countBrokers = 0;
            Integer cycleId = leadOfferService.getMaxCycleIdAndPhaseId(lead.getId(), maxPhaseIdForRequestMoreBrokers);
            int cycleIdInt;

            if (cycleId == null) {
                cycleId = 0;
            }

            if (cycleId > 0) {
                cycleIdInt = cycleId;
            }
            else {
                cycleIdInt = 0;
            }

            Integer countLeadOfferInDB = (int) (long) leadOfferDao.findByLeadIdAndPhaseId(
                    lead.getId(),
                    maxPhaseIdMapLeadId.get(lead.getId()));

            if ((countLeadOfferInDB < PropertyReader
                    .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_MAX_OFFERS_IN_PHASE) || flagRequest == 1)) {
                for (Company company : brokerCompanies) {
                    LeadOffer offer = leadOfferService.offerLeadToBroker(
                            lead,
                            company,
                            cycleIdInt + 1,
                            maxPhaseIdMapLeadId.get(lead.getId()));

                    if (offer != null) {
                        isAssigned = true;
                        notificationService.sendLeadOfferNotification(offer.getId());
                    }
                    countBrokers++;

                    if (countBrokers >= PropertyReader.getRequiredPropertyAsType(
                            PropertyKeys.MARKETPLACE_BROKERS_PER_CYCLE,
                            Integer.class) || (countLeadOfferInDB + countBrokers >= PropertyReader
                                    .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_MAX_OFFERS_IN_PHASE))) {
                        break;
                    }
                }
            }
        }
        if (!isAssigned) {
            throw new ProAPIException("Error in Assigning lead id: " + leadId);
        }
    }

    private List<Company> getBrokersForLeadWithCycleExcludingAlreadyOffered(Lead lead) {
        List<Company> brokers;
        List<Integer> localityIds = getLocalitiesForLead(lead.getId());

        if (localityIds.size() == 0) {
            throw new ProAPIException("No locality found in lead");
        }
        else {
            brokers = companyService.getBrokersForLocalities(localityIds);
        }

        List<Integer> agentIds = new ArrayList<Integer>();

        List<LeadOffer> leadOffers = leadOfferDao.findByLeadId(lead.getId());
        if (!leadOffers.isEmpty()) {
            lead.setLeadOffers(leadOffers);
            for (LeadOffer leadOffer : lead.getLeadOffers()) {
                agentIds.add(leadOffer.getAgentId());
            }
        }

        List<Company> brokerToConsider = new ArrayList<Company>();

        if (!agentIds.isEmpty()) {
            List<Company> brokersToExclude = companyService.getCompanyFromUserId(agentIds);
            List<Integer> brokerIds = new ArrayList<Integer>();

            for (Company broker : brokersToExclude) {
                brokerIds.add(broker.getId());
            }

            for (Company broker : brokers) {
                if (!brokerIds.contains(broker.getId())) {
                    brokerToConsider.add(broker);
                }
            }
        }
        else {
            brokerToConsider = brokers;
        }
        return brokerToConsider;
    }

    @Async
    public void manageLeadAuctionAsync(int leadId) {
        manageLeadAuctionWithBeforeCycle(leadId);
    }

    /**
     * gets all localities for a particular lead
     * 
     * @param lead
     * @return {@link Integer} {@link List}
     */
    @Transactional
    private List<Integer> getLocalitiesForLead(int leadId) {
        List<Integer> localityIds = new ArrayList<>();
        Lead lead = leadDao.findRequirementsByLeadId(leadId);

        for (LeadRequirement requirement : lead.getRequirements()) {
            requirement.setLead(null);
            if (requirement.getLocalityId() != null) {
                localityIds.add(requirement.getLocalityId());
            }
            else if (requirement.getProjectId() != null) {
                localityIds.add(projectService.getProjectDetail(requirement.getProjectId()).getLocalityId());
            }
            else {
                logger.error("No locality found in marketplace lead id : " + leadId);
            }
        }
        return localityIds;
    }

    /**
     * 
     * @param lead
     *            this function first checks if the lead exists in the system on
     *            the basis of user email and phone number and city if
     *            exists(check exists function) then patch(see patch function)
     *            the lead otherwise if client id is not provided it creates or
     *            get new user using user service (for details see user service
     *            create user) and also check if exact replica of
     *            lead_requirements is present in the leads. if yes then it
     *            does'nt insert otherwise it inserts new row in lead
     *            requirements after that it inserts in lead submission.
     * @return lead
     * 
     */

    private void validationOfProjectLocality(LeadRequirement leadRequirement) {
        if (leadRequirement.getLocalityId() != null && localityService.getLocality(leadRequirement.getLocalityId()) == null) {
            throw new BadRequestException("Localities should be valid");
        }
        if (leadRequirement.getProjectId() != null && projectService.getProjectDetails(leadRequirement.getProjectId()) == null) {
            throw new BadRequestException("Project should be valid");
        }
    }

    public Lead createLead(Lead lead) {
        if (lead.getCityId() == 0) {
            throw new BadRequestException("CityId is mandatory");
        }

        if (cityService.getCity(lead.getCityId()) == null) {
            throw new BadRequestException("CityId should be valid");
        }

        lead.setRequirements(removeDuplicateEntries(lead.getRequirements()));

        // Setting isregistered false for all such users
        lead.getClient().setRegistered(false);

        User user = userService.createUser(lead.getClient());
        lead.setClient(user);
        lead.setClientId(user.getId());
        Lead leadOriginal = (Lead) SerializationUtils.clone(lead);
        Lead existingLead = getExistingLead(user.getId(), lead.getCityId());
        if (existingLead != null) {
            lead.setId(existingLead.getId());
            patchLead(existingLead, lead);
            notificationService.createLeadNotification(lead, NotificationType.DuplicateLead.getId());
        }
        else {
            createDump(lead);
        }

        int leadId = lead.getId();
        leadOriginal.setMergedLeadId(leadId);
        createDump(leadOriginal);

        return lead;
    }

    /**
     * 
     * @param lead
     * @return put the lead and lead requirements directly into db
     */

    private void createDump(Lead lead) {
        lead.setId(leadDao.save(lead).getId());
        for (LeadRequirement leadRequirement : lead.getRequirements()) {
            leadRequirement.setLeadId(lead.getId());
            if (!isExactReplica(leadRequirement)) {
                validationOfProjectLocality(leadRequirement);
                leadRequirementsService.save(leadRequirement);
            }
        }
    }

    /**
     * 
     * @param leadRequirement
     *            checks if all fields except id are matching or not in
     *            lead_requirements
     * @return boolean
     */

    private boolean isExactReplica(LeadRequirement leadRequirement) {
        List<LeadRequirement> leadRequirementList = leadRequirementsService.getRequirements(
                leadRequirement.getBedroom(),
                leadRequirement.getLocalityId(),
                leadRequirement.getProjectId(),
                leadRequirement.getLeadId());

        return !leadRequirementList.isEmpty();
    }

    /**
     * 
     * @param lead
     *            patch leads when there is duplicate lead already present .
     *            gets lead and set client id set lead id ,checks replica save
     *            lead_requirements and then save lead_submissions and set range
     *            from min size to max size till now and min budget to max
     *            budget till now and update user email and phone number
     *            according to duplicacy.
     * 
     * @return
     */
    private void patchLead(Lead existingLead, Lead lead) {
        for (LeadRequirement leadRequirement : lead.getRequirements()) {
            leadRequirement.setLeadId(existingLead.getId());
            if (!isExactReplica(leadRequirement)) {
                validationOfProjectLocality(leadRequirement);
                leadRequirementsService.saveAndFlush(leadRequirement);
            }
        }

        existingLead.setMinSize(UtilityClass.min(existingLead.getMinSize(), lead.getMinSize()));
        existingLead.setMaxSize(UtilityClass.max(existingLead.getMaxSize(), lead.getMaxSize()));
        existingLead.setMinBudget(UtilityClass.min(existingLead.getMinBudget(), lead.getMinBudget()));
        existingLead.setMaxBudget(UtilityClass.max(existingLead.getMaxBudget(), lead.getMaxBudget()));

        leadDao.save(existingLead);
    }

    /**
     * 
     * @param email
     * @param contactNumber
     * @param cityId
     *            gets user according to email or contact number and city id. it
     *            checks weather in that city there exists a lead for which
     *            there exists entry in lead offers in which status_id is not in
     *            deal closed,closed lost,dead which is then mapped with
     *            master_source . after that it checks weather there exists a
     *            lead which is not broadcasted is present otherwise it returns
     *            false
     * @return
     */
    private Lead getExistingLead(int userId, int cityId) {
        List<Lead> leads = leadDao.getLeads(cityId, userId);
        Lead existingLead = null;

        if (!leads.isEmpty()) {
            Lead lead = leads.get(0);
            List<LeadOffer> leadOffers = leadOfferDao.getLeadOffers(lead.getId());

            if (leadOffers.isEmpty()) {
                existingLead = lead;
            }
            else {
                for (LeadOffer leadOffer : leadOffers) {
                    if (leadOffer.getMasterLeadOfferStatus().isOpen()) {
                        existingLead = lead;
                        break;
                    }
                }
            }
        }

        return existingLead;
    }

    private boolean areRequirementsEqual(LeadRequirement leadRequirement, LeadRequirement otherLeadRequirement) {
        Integer bedroom = leadRequirement.getBedroom();
        Integer otherBedroom = otherLeadRequirement.getBedroom();
        Integer localityId = leadRequirement.getLocalityId();
        Integer otherLocalityId = otherLeadRequirement.getLocalityId();
        Integer projectId = leadRequirement.getProjectId();
        Integer otherProjectId = otherLeadRequirement.getProjectId();

        if (((bedroom == null && otherBedroom == null) || (otherBedroom != null && otherBedroom.equals(bedroom))) && ((localityId == null && otherLocalityId == null) || (otherLocalityId != null && otherLocalityId
                .equals(localityId)))
                && ((projectId == null && otherProjectId == null) || (otherProjectId != null && otherProjectId
                        .equals(projectId)))) {
            return true;
        }
        else {
            return false;
        }
    }

    private List<LeadRequirement> removeDuplicateEntries(List<LeadRequirement> requirements) {
        int i = 0;
        List<LeadRequirement> uniqueList = new ArrayList<LeadRequirement>();

        for (LeadRequirement leadRequirement : requirements) {
            boolean isDuplicate = false;

            for (int j = 0; j < i; j++) {
                if (areRequirementsEqual(leadRequirement, requirements.get(j))) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                uniqueList.add(leadRequirement);
            }

            i++;
        }

        return uniqueList;
    }

    /**
     * @param email
     * @param contactNumber
     *            checks weather there exist a lead for some specific user with
     *            email or contact number *
     * @param cityId
     * @return
     */
    public boolean exists(String email, int cityId) {
        User user = userService.getUser(email);
        return (user != null) && (getExistingLead(user.getId(), cityId) != null);
    }

    public List<Lead> getLeads(List<Integer> leadIds) {
        return leadDao.getLeads(leadIds);
    }

    public void updateRequestMoreBrokers(int leadId) {
        Lead leadInDB = leadDao.findById(leadId);
        Integer maxPhaseId = leadOfferDao.getMaxPhaseId(leadId);
        leadInDB.setRequestBrokerPhaseId(maxPhaseId);
        leadDao.save(leadInDB);
    }
}