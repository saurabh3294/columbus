package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.LeadTask;

/**
 * 
 * @author azi
 * 
 */
public interface LeadTaskDao extends JpaRepository<LeadTask, Integer> {
    public List<LeadTask> findByLeadOfferId(int leadOfferId);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN LT.leadOffer LO INNER JOIN LT.taskStatus LTS INNER JOIN LTS.masterLeadTask MLT INNER JOIN LTS.masterLeadTaskStatus MLTS WHERE LO.agentId = ?1")
    public List<LeadTask> getLeadTasksForUser(int userId, Pageable pageable);

    @Query(
            value = "SELECT COUNT(LT) FROM LeadTask LT INNER JOIN LT.leadOffer LO INNER JOIN LT.taskStatus LTS INNER JOIN LTS.masterLeadTask MLT INNER JOIN LTS.masterLeadTaskStatus MLTS WHERE LO.agentId = ?1")
    public long getLeadTaskCountForUser(int userId);
}