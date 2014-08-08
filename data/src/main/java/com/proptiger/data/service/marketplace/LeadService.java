package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.model.user.User;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadRequirementsDao;
import com.proptiger.data.repo.marketplace.LeadSubmissionsDao;
import com.proptiger.data.service.user.UserService;

/**
 * @author Anubhav
 * 
 */
@Service
public class LeadService {
    @Autowired
    private UserService          userService;

    @Autowired
    private LeadDao              leadDao;

    @Autowired
    private LeadRequirementsDao  leadRequirementsDao;

    @Autowired
    private LeadSubmissionsDao   leadSubmissionsDao;

    public List<Lead> getLeads(FIQLSelector fiqlSelector) {
        return null;
    }

    
    /**
     * 
     * @param lead
     * this function first checks if the lead exists in the system on the basis of user email and phone number and city
     * if exists(check exists function) then patch(see patch function) the lead otherwise if client id is not provided
     * it creates or get new user using user service (for details see user service create user) and also check if exact 
     * replica of lead_requirements is present in the leads. if yes then it does'nt insert otherwise it inserts new 
     * row in lead requirements after that it inserts in lead submission. 
     * @return lead
     * 
     */
    
    public Lead createLead(Lead lead) {
        if (exists(lead.getClient().getEmails().get(0).getEmail(), lead.getClient().getContactNumbers().get(0)
                .getContactNumber(), lead.getCityId())) {
            
            lead = patchLead(lead);
        }
        else {
            if (lead.getClientId() == 0) {
                lead.setClient(userService.createUser(lead.getClient()));
                lead.setClientId(lead.getClient().getId());                                                     
            }
            lead.setClient(userService.setUserIdAndCreatedByOfClientForEmailsAndContactNumbersAndSave(lead.getClient(),lead.getClientId(),1));
            lead.setId(leadDao.save(lead).getId());
            
            if(!exactReplica(lead.getLeadRequirements().get(0)))
            {
                lead.getLeadRequirements().get(0).setLeadId(lead.getId());
                
                lead.getLeadRequirements().get(0).setMinSize(lead.getMinSize());
                lead.getLeadRequirements().get(0).setMaxSize(lead.getMaxSize());
                lead.getLeadRequirements().get(0).setMinBudget(lead.getMinBudget());
                lead.getLeadRequirements().get(0).setMaxBudget(lead.getMaxBudget());
                lead.getLeadRequirements().get(0).setId(leadRequirementsDao.save(lead.getLeadRequirements().get(0)).getId());
            }
            
            lead.getLeadSubmissions().get(0).setLeadRequirementId(lead.getLeadRequirements().get(0).getId());
            lead.getLeadSubmissions().get(0).setLeadId(lead.getId());
            leadSubmissionsDao.save(lead.getLeadSubmissions().get(0));
        }
        
        return lead;
    }

    /**
     * 
     * @param leadRequirement
     * checks if all fields except id are matching or not in lead_requirements
     * @return boolean
     */
    
    public boolean exactReplica(LeadRequirement leadRequirement)
    {
        List<LeadRequirement> leadRequirementList = leadRequirementsDao.checkReplica(leadRequirement.getBedroom(),leadRequirement.getLocalityId(),leadRequirement.getProjectId(),leadRequirement.getMinSize(),leadRequirement.getMaxSize(),leadRequirement.getMinBudget(),leadRequirement.getMaxBudget());         
        if(leadRequirementList.size() > 0)
        { 
            leadRequirement = leadRequirementList.get(0);
            return true;
        }
        else
            return false;    
    }
    
    /**
     * 
     * @param lead
     * patch leads when there is duplicate lead already present . gets lead and set client id
     * set lead id ,checks replica save lead_requirements and then save lead_submissions and set range 
     * from min size to max size till now and min budget to max budget till now and update user email and 
     * phone number according to duplicacy (see setUserIdAndCreatedByOfClientForEmailsAndContactNumbersAndSave() in user service)
     *   
     * @return
     */
    
    
    private Lead patchLead(Lead lead) {
        
         Lead tmpLead = getExistingLead(lead.getClient().getEmails().get(0).getEmail(), lead.getClient().getContactNumbers().get(0)
                 .getContactNumber(), lead.getCityId());
        
         int existingLeadId = tmpLead.getId();
         lead.setClientId(tmpLead.getClientId());
         
        lead.setId(existingLeadId);
        
        if(!exactReplica(lead.getLeadRequirements().get(0)))
        {     lead.getLeadRequirements().get(0).setLeadId(existingLeadId);
              leadRequirementsDao.saveAndFlush(lead.getLeadRequirements().get(0));
        }
        
        lead.getLeadSubmissions().get(0).setLeadRequirementId(lead.getLeadRequirements().get(0).getId());
        lead.getLeadSubmissions().get(0).setLeadId(existingLeadId);
        leadSubmissionsDao.saveAndFlush(lead.getLeadSubmissions().get(0));
        
        List<Lead> dataLeads = leadDao.findByClientId(lead.getClientId());
            
        Lead data = dataLeads.get(0);
        
        if(lead.getMinSize() > data.getMinSize())
        {
            lead.setMinSize(data.getMinSize());
        }
        
        if(lead.getMaxSize() < data.getMaxSize())
        {
            lead.setMaxSize(data.getMaxSize());
        }
        
        if(lead.getMinBudget() < data.getMinBudget())
        {
            lead.setMinBudget(data.getMinBudget());
        }
        
        if(lead.getMaxBudget() > data.getMaxBudget())
        {
            lead.setMaxBudget(data.getMaxBudget());
        }
        
        leadDao.save(lead);
        userService.setUserIdAndCreatedByOfClientForEmailsAndContactNumbersAndSave(lead.getClient(),lead.getClientId(),0);
        
                
        return lead;
    }

    /**
     * 
     * @param email
     * @param contactNumber
     * @param cityId
     * gets user according to email or contact number and city id. it checks weather in that city there 
     * exists a lead for which there exists entry in lead offers in which status_id is not 7,8,9 which is 
     * then mapped with master_source . after that it checks weather there exists a lead which is not broadcasted
     * is present otherwise it returns false
     * @return
     */
    
    public boolean exists(String email, String contactNumber, int cityId) {
                        
            User user = userService.getUser(email, contactNumber);  
                        
            boolean duplicacy = false;
            if (user != null) {
                 if(leadDao.getDuplicateLead(cityId,user.getId()).size() > 0)
                 {
                     return true;
                 }
                 else if(leadDao.checkDuplicateLead(cityId,user.getId()).size() > 0)
                 {
                     return true;
                 }
                 else
                 {
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
     * same as exist function but it returns user
     * @return
     */
    
    
    public Lead getExistingLead(String email, String contactNumber, int cityId) {
        
        User user = userService.getUser(email, contactNumber);  
        if (user != null) {
             //return leadDao.checkDuplicateLead(cityId,user.getId()).get(0);
            List<Lead> tmpLead = leadDao.getDuplicateLead(cityId,user.getId());
            List<Lead> tmpLeadOverAll = leadDao.checkDuplicateLead(cityId,user.getId());
            if(tmpLead.size() > 0)
            {
                return tmpLead.get(0);
            }
            else if(tmpLeadOverAll.size() > 0)
            {
                return tmpLeadOverAll.get(0);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
       
}
