package com.proptiger.data.repo.marketplace;

import java.util.List;

import com.proptiger.data.model.marketplace.LeadRequirement;

public interface LeadRequirementsCustomDao {
    public List<LeadRequirement> fetchRequirements(Integer bedroom, Integer localityId, Integer projectId, int leadId);
}
