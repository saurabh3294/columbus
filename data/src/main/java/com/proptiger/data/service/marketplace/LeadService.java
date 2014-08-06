/**
 * 
 */
package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.user.User;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadRequirementsDao;
import com.proptiger.data.repo.marketplace.LeadSubmissionsDao;
import com.proptiger.data.service.user.UserService;
import com.proptiger.exception.BadRequestException;

/**
 * @author mandeep
 *
 */
@Service
public class LeadService {
    @Autowired
    private UserService userService;
    
    @Autowired
    private LeadDao     leadDao;
    
    private int dealClosed = 9;
    private int dead = 7;
    private int closedLost = 8;
    
    private List<Lead> leadList;
    
    @Autowired
    private LeadRequirementsDao leadRequirementsDao;
    
    @Autowired
    private LeadSubmissionsDao leadSubmissionsDao;
    
    public List<Lead> getLeads(FIQLSelector fiqlSelector) {
           return null;
    }
    
    public Lead createLead(Lead lead) {
        if (lead.getClientId() == 0) {
            lead.setClient(userService.createUser(lead.getClient()));
            lead.setClientId(lead.getClient().getId());
        }

        if (exists(lead.getClient().getEmails().get(0).getEmail(),lead.getClient().getContactNumbers().get(0).getContactNumber(),lead.getCityId())) {            
            patchLead(lead);
        }
        else {
            leadDao.save(lead);
        }

        return null;
    }

    private void patchLead(Lead lead) {
        lead.getLeadRequirements().get(0).setLeadId(leadList.get(0).getId());
        lead.getLeadSubmissions().get(0).setLeadId(leadList.get(0).getId());
                
        leadRequirementsDao.save(lead.getLeadRequirements().get(0));
        leadSubmissionsDao.save(lead.getLeadSubmissions().get(0));
    }


    public boolean exists(String email, String contactNumber, int cityId) {
        
        //try 
        {
            if (cityId == 0) {
                throw new BadRequestException(ResponseCodes.BAD_REQUEST, "Please provide City id");
            }

            if (email == null && contactNumber == null) {
                throw new BadRequestException(ResponseCodes.BAD_REQUEST, "Please provide Email or phone");
            }

            boolean duplicateOrNot;

            User user = null;
            if (contactNumber != "" && email != "" && cityId != 0) {               
                duplicateOrNot = userService.isRegisteredUser(email, contactNumber);
                if (duplicateOrNot == true) {
                    user = userService.getUserFromEmailAndPhone(email, contactNumber);
                }
            }
            else if (email != "" && cityId != 0) {
                
                duplicateOrNot = userService.isRegisteredEmail(email);

                if (duplicateOrNot == true) {
                    user = userService.getUserFromEmail(email);
                }
            }
            else if (contactNumber != "" && cityId != 0) {
                duplicateOrNot = userService.isRegisteredPhone(contactNumber);
                if (duplicateOrNot == true) {
                    user = userService.getUserFromPhone(contactNumber);
                }
            }
            else {
                throw new BadRequestException(ResponseCodes.BAD_REQUEST, "Please provide Email or phone number");
            }

            boolean duplicacy = false;
            if (duplicateOrNot == true) {
                leadList = getLeadsData(user.getId());
                duplicacy = getDuplicacyStatus(leadList, cityId);
            }
            else {
                duplicacy = false;
            }

            return duplicacy;
        }
        
        
    }
    
    public List<Lead> getLeadsData(int Id) {       
        List<Lead> data = leadDao.findByclientId(Id);        
        return data;    
    }
    
    public boolean getDuplicacyStatus(List<Lead> leadList,int cityId)
    {
        boolean duplicacy = false;
        for (Lead l:leadList)
        {                       
            if(l.getCityId() == cityId)
            {
                    for(LeadOffer lo: l.getLeadOffers())
                    {
                        int statusId = lo.getStatusId();
                        if(statusId == closedLost || statusId == dead || statusId == dealClosed)
                        {
                            duplicacy = false;
                        }
                        else
                        {
                            duplicacy = true;
                            break;
                        }
                    }
                    if(duplicacy == true)
                    {
                        break;
                    }
            }
            else
            {
                duplicacy = false;
            }
        }
    
    return duplicacy;
    
    }    
}
