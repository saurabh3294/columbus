package com.proptiger.data.service.marketplace;

import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.user.User;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadRequirementsDao;
import com.proptiger.data.service.LeadOfferStatus;
import com.proptiger.data.service.user.UserService;

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
    private LeadRequirementsDao leadRequirementsDao;

    @Autowired
    private LeadOfferDao        leadOfferDao;

    public List<Lead> getLeads(FIQLSelector fiqlSelector, int integer) {
        return null;
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
        }
        else {
            lead.setId(leadDao.save(lead).getId());
            LeadRequirement leadRequirement = lead.getRequirements().get(0);
            if (!isExactReplica(leadRequirement)) {
                leadRequirement.setLeadId(lead.getId());
                leadRequirementsDao.save(leadRequirement);
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
        LeadRequirement leadRequirement = lead.getRequirements().get(0);
        leadRequirement.setLeadId(lead.getId());
        leadRequirementsDao.save(leadRequirement);
    }

    /**
     * 
     * @param leadRequirement
     *            checks if all fields except id are matching or not in
     *            lead_requirements
     * @return boolean
     */

    private boolean isExactReplica(LeadRequirement leadRequirement) {
        List<LeadRequirement> leadRequirementList = leadRequirementsDao.checkReplica(
                leadRequirement.getBedroom(),
                leadRequirement.getLocalityId(),
                leadRequirement.getProjectId());

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
            if (!isExactReplica(leadRequirement)) {
                leadRequirement.setLeadId(existingLead.getId());
                leadRequirementsDao.saveAndFlush(leadRequirement);
            }
        }

        existingLead.setMinSize(Math.min(existingLead.getMinSize(), lead.getMinSize()));
        existingLead.setMaxSize(Math.max(existingLead.getMaxSize(), lead.getMaxSize()));
        existingLead.setMinBudget(Math.min(existingLead.getMinBudget(), lead.getMinBudget()));
        existingLead.setMaxBudget(Math.max(existingLead.getMaxBudget(), lead.getMaxBudget()));
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
                    if (statusId != LeadOfferStatus.Dead.getId() && 
                        statusId != LeadOfferStatus.ClosedLost.getId() && 
                        statusId != LeadOfferStatus.ClosedWon.getId())
                    {
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
     *        checks weather there exist a lead for some specific user with email or contact number     *         
     * @param cityId
     * @return
     */
    public boolean exists(String email, String contactNumber, int cityId) {
        User user = userService.getUser(email, contactNumber);
        return (user != null) && (getExistingLead(user.getId(), cityId) != null);
    }

}
