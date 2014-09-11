package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.data.model.marketplace.LeadRequirement;

public interface LeadRequirementsCustomDao {
    public List<LeadRequirement> getRequirements(Integer bedroom, Integer localityId, Integer projectId, Integer leadId);
}
