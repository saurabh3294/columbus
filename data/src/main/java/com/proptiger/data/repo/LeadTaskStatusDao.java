package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.LeadTaskStatus;

/**
 * 
 * @author azi
 * 
 */
public interface LeadTaskStatusDao extends JpaRepository<LeadTaskStatus, Integer> {
    @Query(
            value = "SELECT LTS from LeadTaskStatus LTS INNER JOIN LTS.masterLeadTaskStatus MLTS INNER JOIN LTS.masterLeadTask MLT WHERE MLT.name = ?1 and MLTS.status = ?2")
    public LeadTaskStatus getLeadTaskStatusFromTaskNameAndStatusName(String taskName, String statusName);
}
