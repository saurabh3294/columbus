package com.proptiger.data.service.marketplace;

import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.UserContactNumber;
import com.proptiger.data.model.user.UserEmail;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadRequirementsDao;
import com.proptiger.data.repo.marketplace.LeadSubmissionsDao;
import com.proptiger.data.repo.marketplace.leadOfferDao;
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
    private leadOfferDao        leadOfferDao;

    public List<Lead> getLeads(FIQLSelector fiqlSelector) {
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

    public Lead createLead(Lead lead) {

        Lead leadDump = (Lead) SerializationUtils.clone(lead);

        UserEmail userEmail = lead.getClient().getEmails().get(0);
        UserContactNumber userContactNumber = lead.getClient().getContactNumbers().get(0);

        User user = userService.createUser(lead.getClient());
        lead.setClient(user);

        if (getExistingLead(user.getId(), lead.getCityId()) != null) {
            lead = patchLead(lead);
        }
        else {
            lead.setClient(user);
            lead.setId(leadDao.save(lead).getId());
            LeadRequirement leadRequirement = lead.getRequirements().get(0);
            
            if (!isExactReplica(leadRequirement)) {
                leadRequirement.setLeadId(lead.getId());
                leadRequirementsDao.save(leadRequirement);
            }
        }
        int leadId = lead.getId();
        leadDump.setMergedLeadId(leadId);
        Lead dump = createDump(leadDump);
        return lead;
    }

    /**
     * 
     * @param lead
     * @return put the lead and lead requirements directly into db
     */

    private Lead createDump(Lead lead) {

        lead.setClient(userService.createUser(lead.getClient()));
        lead.setClientId(lead.getClient().getId());
        lead.setId(leadDao.save(lead).getId());

        LeadRequirement leadRequirement = lead.getRequirements().get(0);

        leadRequirement.setLeadId(lead.getId());
        leadRequirementsDao.save(leadRequirement);
        return lead;
    }

    /**
     * 
     * @param leadRequirement
     *            checks if all fields except id are matching or not in
     *            lead_requirements
     * @return boolean
     */

    public boolean isExactReplica(LeadRequirement leadRequirement) {
        List<LeadRequirement> leadRequirementList = leadRequirementsDao.checkReplica(
                leadRequirement.getBedroom(),
                leadRequirement.getLocalityId(),
                leadRequirement.getProjectId());
        if (leadRequirementList.size() > 0) {
            return true;
        }
        else
            return false;
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
    private Lead patchLead(Lead lead) {

        UserEmail userEmail = lead.getClient().getEmails().get(0);
        UserContactNumber userContactNumber = lead.getClient().getContactNumbers().get(0);

        Lead tmpLead = getExistingLead(lead.getClient().getId(), lead.getCityId());
        lead.setId(tmpLead.getId());

        LeadRequirement leadRequirement = lead.getRequirements().get(0);

        if (!isExactReplica(leadRequirement)) {
            leadRequirement.setLeadId(tmpLead.getId());
            leadRequirementsDao.saveAndFlush(leadRequirement);
        }

        List<Lead> dataLeads = leadDao.findByClientId(lead.getClientId());
        Lead data = dataLeads.get(0);
         lead.setMinSize(Math.min(data.getMinSize(), lead.getMinSize()));
         lead.setMaxSize(Math.max(data.getMaxSize(), lead.getMaxSize()));
         lead.setMinBudget(Math.min(data.getMinBudget(), lead.getMinBudget()));
         lead.setMaxBudget(Math.max(data.getMaxBudget(), lead.getMaxBudget()));

        leadDao.save(lead);
        userService.patchUser(lead.getClient());
        return lead;
    }

    /**
     * 
     * @param email
     * @param contactNumber
     * @param cityId
     *            gets user according to email or contact number and city id. it
     *            checks weather in that city there exists a lead for which
     *            there exists entry in lead offers in which status_id is not
     *            7,8,9 which is then mapped with master_source . after that it
     *            checks weather there exists a lead which is not broadcasted is
     *            present otherwise it returns false
     * @return
     */

    public Lead getExistingLead(int userId, int cityId) {

            List<Lead> existingLeadwithStatusNotInDealClosedOrClosedLostOrDead = leadDao.getOpenLeadOffers(cityId, userId);
            List<Lead> existingLeadOverAll = leadDao.checkDuplicateLead(cityId, userId);
            if (existingLeadwithStatusNotInDealClosedOrClosedLostOrDead.size() > 0) {
                return existingLeadwithStatusNotInDealClosedOrClosedLostOrDead.get(0);
            }
            else if (existingLeadOverAll.size() > 0 && leadDao.getLeadOffers(cityId, userId).size() == 0) {
                return existingLeadOverAll.get(0);
            }
            else {
                return null;
            }
    }

}
