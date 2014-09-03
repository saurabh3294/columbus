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

    @Query("select LR from LeadRequirement LR left join fetch LR.project LRP left join fetch LRP.projectStatusMaster left join fetch LR.locality LRL left join fetch LRL.suburb LRLS left join fetch LRLS.city left join fetch LRP.builder LRPB left join fetch LRP.locality LRPL where (LRP.version is null or LRP.version ='Website') and LR.leadId in (?1) order by LR.projectId desc")
    public List<LeadRequirement> findByLeadIdIn(List<Integer> leadIds);

    @Query("select LR from LeadRequirement LR join LR.lead LRL join LRL.leadOffers LRLO left join fetch LR.project LRP left join fetch LRP.projectStatusMaster left join fetch LR.locality LRL left join fetch LRL.suburb LRLS left join fetch LRLS.city left join fetch LRP.builder LRPB left join fetch LRP.locality LRPL where (LRP.version is null or LRP.version ='Website') and LRLO.id = ?1 order by LR.projectId desc")
    public List<LeadRequirement> getRequirementsByLeadOfferId(int leadOfferId);    
}
