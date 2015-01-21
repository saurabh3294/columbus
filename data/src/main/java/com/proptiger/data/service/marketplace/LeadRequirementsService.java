/**
 * 
 */
package com.proptiger.data.service.marketplace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.marketplace.LeadRequirement;
import com.proptiger.data.repo.marketplace.LeadRequirementsDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class LeadRequirementsService {
    @Autowired
    private LeadRequirementsDao leadRequirementsDao;

    public List<LeadRequirement> getRequirements(List<Integer> leadIds) {
        return leadRequirementsDao.findByLeadIdIn(leadIds);
    }

    public void save(LeadRequirement leadRequirement) {
        leadRequirementsDao.save(leadRequirement);
    }

    public List<LeadRequirement> getRequirements(
            Integer bedroom,
            Integer localityId,
            Integer projectId,
            Integer propertyTypeId,
            int leadId) {
        return leadRequirementsDao.fetchRequirements(bedroom, localityId, projectId, propertyTypeId, leadId);
    }

    public void saveAndFlush(LeadRequirement leadRequirement) {
        leadRequirementsDao.saveAndFlush(leadRequirement);
    }

    public List<LeadRequirement> getRequirements(int leadOfferId) {
        return leadRequirementsDao.getRequirementsByLeadOfferId(leadOfferId);
    }
}
