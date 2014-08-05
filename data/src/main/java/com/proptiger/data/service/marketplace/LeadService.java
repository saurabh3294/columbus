/**
 * 
 */
package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.marketplace.Lead;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.service.user.UserService;

/**
 * @author mandeep
 *
 */
public class LeadService {
    @Autowired
    private UserService userService;
    
    @Autowired
    private LeadDao leadDao;
    
    public List<Lead> getLeads(FIQLSelector fiqlSelector) {
        return null;
    }
    
    public Lead createLead(Lead lead) {
        if (lead.getClientId() == 0) {
            lead.setClient(userService.createUser(lead.getClient()));
            lead.setClientId(lead.getClient().getId());
        }

        if (exists(lead)) {
            patchLead(lead);
        }
        else {
            // leadDao.save(lead);
        }

        return null;
    }

    private void patchLead(Lead lead) {
        // TODO Auto-generated method stub
        
    }

    private boolean exists(Lead lead) {
        // leadDao.findAll();
        // TODO Auto-generated method stub
        return false;
    }
}
