package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.service.LeadOfferStatus;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.companyuser.CompanyService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.ProAPIException;

/**
 * @author Anubhav
 * @param
 * 
 */
@Service
public class LeadService {
    @Autowired
    private UserService         userService;

    @Autowired
    private LeadDao             leadDao;

    @Autowired
    private LeadRequirementsService leadRequirementsService;

    @Autowired
    private LeadOfferDao        leadOfferDao;

    @Autowired
    private LeadOfferService    leadOfferService;

    @Autowired
    private ProjectService      projectService;

    @Autowired
    private CompanyService      companyService;

    @Autowired
    private PropertyReader      propertyReader;

    private static Logger       logger = LoggerFactory.getLogger(LeadService.class);

    @Autowired
    private NotificationService notificationService;
    
    
    @Transactional
    public void manageLeadAuction(int leadId) {
        Lead lead = leadDao.findOne(leadId);
        boolean biddingCycleOver = (lead.getLeadOffers().size() != 0);

        if (biddingCycleOver) {
            // communication to channel manager
            lead.setNextActionTime(null);
            leadDao.save(lead);
        }
        else {
            List<Company> brokerCompanies = getBrokersForLead(lead.getId());
            if (brokerCompanies.size() == 0) {
                // error case of no broker found
            }
            else {
                for (Company company : brokerCompanies) {
                    leadOfferService.offerLeadToBroker(lead, company, 1);
                }
                lead.setNextActionTime(DateUtil.getWorkingTimeAddedIntoDate(new Date(), propertyReader
                        .getRequiredPropertyAsType(PropertyKeys.MARKETPLACE_BIDDING_CYCLE_DURATION, Integer.class)));
                leadDao.save(lead);
            }
        }
    }

    public List<Lead> getLeadsPendingAction() {
        return leadDao.findByNextActionTimeLessThan(new Date());
    }

    /**
     * gets all broker companies eligible to fulfil a lead
     * 
     * @param lead
     * @return {@link Company} {@link List}
     */
    private List<Company> getBrokersForLead(int leadId) {
        List<Company> brokers = new ArrayList<>();
        Lead lead = leadDao.findOne(leadId);
        List<Integer> localityIds = getLocalitiesForLead(lead.getId());
        if (localityIds.size() == 0) {
            throw new ProAPIException("No locality found in lead");
        }
        else {
            brokers = companyService.getBrokersForLocalities(localityIds);
        }

        logger.debug("BROKERS FOR LEAD-ID: " + lead.getId() + " ARE: " + new Gson().toJson(brokers));
        return brokers;
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
        Lead lead = leadDao.findOne(leadId);
        for (LeadRequirement requirement : lead.getRequirements()) {
            if (requirement.getLocalityId() != null) {
                localityIds.add(requirement.getLocalityId());
            }
            else if (requirement.getProjectId() != null) {
                localityIds.add(projectService.getProjectDetail(requirement.getProjectId()).getLocalityId());
            }
            else {
                // Some error case
            }
        }

        logger.debug("LOCALITIES IN LEAD " + leadId + " ARE " + new Gson().toJson(localityIds));
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
    @Transactional
    public Lead createLead(Lead lead) {
        User user = userService.createUser(lead.getClient());
        lead.setClient(user);
        lead.setClientId(user.getId());
        Lead leadOriginal = (Lead) SerializationUtils.clone(lead);
        Lead existingLead = getExistingLead(user.getId(), lead.getCityId());
        if (existingLead != null) {
            lead.setId(existingLead.getId());
            patchLead(existingLead, lead);
            notificationService.createLeadNotification(lead, 3);            
        }
        else {
            lead.setId(leadDao.save(lead).getId());            
                for (LeadRequirement leadRequirement : lead.getRequirements()) {
                        leadRequirement.setLeadId(lead.getId());
                        leadRequirementsService.save(leadRequirement);
                }
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
            leadRequirementsService.save(leadRequirement);
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
            leadRequirement.setLeadId(lead.getId());
            if (!isExactReplica(leadRequirement)) {
                leadRequirement.setLeadId(existingLead.getId());
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
                    int statusId = leadOffer.getStatusId();
                    if (statusId != LeadOfferStatus.Dead.getId() && statusId != LeadOfferStatus.ClosedLost.getId()
                            && statusId != LeadOfferStatus.ClosedWon.getId()) {
                        existingLead = lead;
                        break;
                    }
                }
            }
        }

        return existingLead;
    }

    /**
     * 
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

}
