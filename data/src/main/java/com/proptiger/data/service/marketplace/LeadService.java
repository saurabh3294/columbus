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
    private LeadSubmissionsDao  leadSubmissionsDao;

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

        if (exists(userEmail.getEmail(), userContactNumber.getContactNumber(), lead.getCityId())) {
            lead = patchLead(lead);
        }
        else {
            lead.setClient(userService.createUser(lead.getClient()));
            lead.setClientId(lead.getClient().getId());
            lead.setId(leadDao.save(lead).getId());

            LeadRequirement leadRequirement = lead.getLeadRequirements().get(0);
            if (!exactReplica(leadRequirement)) {
                leadRequirement.setLeadId(lead.getId());
                leadRequirementsDao.save(leadRequirement);
            }
        }
        int leadId = lead.getId();
        leadDump.setMergedLeadId(leadId);
        Lead dump = createDump(leadDump);
        return lead;
    }

    private Lead createDump(Lead lead) {
                
        lead.setClient(userService.createUser(lead.getClient()));
        lead.setClientId(lead.getClient().getId());
        lead.setId(leadDao.save(lead).getId());

        LeadRequirement leadRequirement = lead.getLeadRequirements().get(0);

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

    public boolean exactReplica(LeadRequirement leadRequirement) {
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

        Lead tmpLead = getExistingLead(userEmail.getEmail(), userContactNumber.getContactNumber(), lead.getCityId());

        lead.setClientId(tmpLead.getClientId());
        lead.setId(tmpLead.getId());

        LeadRequirement leadRequirement = lead.getLeadRequirements().get(0);

        if (!exactReplica(leadRequirement)) {
            leadRequirement.setLeadId(tmpLead.getId());
            leadRequirementsDao.saveAndFlush(leadRequirement);
        }

        List<Lead> dataLeads = leadDao.findByClientId(lead.getClientId());
        Lead data = dataLeads.get(0);

        if (lead.getMinSize() > data.getMinSize()) {
            lead.setMinSize(data.getMinSize());
        }
        if (lead.getMaxSize() < data.getMaxSize()) {
            lead.setMaxSize(data.getMaxSize());
        }
        if (lead.getMinBudget() < data.getMinBudget()) {
            lead.setMinBudget(data.getMinBudget());
        }
        if (lead.getMaxBudget() > data.getMaxBudget()) {
            lead.setMaxBudget(data.getMaxBudget());
        }

        leadDao.save(lead);
        userService.patchUser(lead.getClient(), lead.getClientId(), 0);
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

    public boolean exists(String email, String contactNumber, int cityId) {

        User user = userService.getUser(email, contactNumber);
        if (user != null) {
            if (leadDao.getDuplicateLead(cityId, user.getId()).size() > 0) {
                return true;
            }
            else if (leadDao.checkDuplicateLead(cityId, user.getId()).size() > 0 && leadDao.checkLeadOfferEntry(
                    cityId,
                    user.getId()).size() == 0) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * 
     * @param email
     * @param contactNumber
     * @param cityId
     *            get existing lead in the system
     * @return user
     */

    public Lead getExistingLead(String email, String contactNumber, int cityId) {

        User user = userService.getUser(email, contactNumber);
        if (user != null) {
            List<Lead> existingLeadwithStatusNotIn789 = leadDao.getDuplicateLead(cityId, user.getId());
            List<Lead> existingLeadOverAll = leadDao.checkDuplicateLead(cityId, user.getId());
            if (existingLeadwithStatusNotIn789.size() > 0) {
                return existingLeadwithStatusNotIn789.get(0);
            }
            else if (existingLeadOverAll.size() > 0 && leadDao.checkLeadOfferEntry(cityId, user.getId()).size() == 0) {
                return existingLeadOverAll.get(0);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

}
