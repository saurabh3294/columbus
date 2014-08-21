package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.LeadRequirement;

public interface LeadRequirementsDao extends JpaRepository<LeadRequirement, Integer>{    
    @Query("select LR from LeadRequirement LR where LR.bedroom = ?1 and LR.localityId = ?2 and LR.projectId = ?3 and LR.leadId = ?4")
    public List<LeadRequirement> getRequirements(
            Integer bedroom,
            Integer localityId,
            Integer projectId,
            Integer leadId
            );

    public List<LeadRequirement> findByLeadIdIn(List<Integer> leadIds);    
}
