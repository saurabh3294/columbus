package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.TaskOfferedListingMapping;

/**
 * 
 * @author azi
 * 
 */
public interface LeadTaskDao extends JpaRepository<LeadTask, Integer> {
    public List<LeadTask> findByLeadOfferId(int leadOfferId);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN FETCH LT.leadOffer LO INNER JOIN FETCH LT.leadOffer LO INNER JOIN FETCH LT.taskStatus LTS INNER JOIN FETCH LTS.masterLeadTask MLT INNER JOIN FETCH LTS.masterLeadTaskStatus MLTS LEFT JOIN FETCH LTS.statusReasons LTSR WHERE LO.agentId = ?1")
    public List<LeadTask> getLeadTasksForUser(int userId, Pageable pageable);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN FETCH LT.leadOffer LO INNER JOIN FETCH LT.leadOffer LO INNER JOIN FETCH LT.taskStatus LTS INNER JOIN FETCH LTS.masterLeadTask MLT INNER JOIN FETCH LTS.masterLeadTaskStatus MLTS LEFT JOIN FETCH LTS.statusReasons LTSR WHERE LT.id = ?1")
    public LeadTask getLeadTaskDetails(int taskId);

    @Query(value = "SELECT COUNT(LT) FROM LeadTask LT INNER JOIN LT.leadOffer LO WHERE LO.agentId = ?1")
    public long getLeadTaskCountForUser(int userId);

    @Query(
            value = "SELECT TOLM FROM LeadTask LT INNER JOIN LT.offeredListingMappings TOLM INNER JOIN FETCH TOLM.offeredListing LOL WHERE LT.id = ?1")
    public List<TaskOfferedListingMapping> getMappedListingMappingsForTask(int taskId);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN FETCH LT.offeredListingMappings TOLM INNER JOIN FETCH TOLM.offeredListing where LT.id IN (?1)")
    public List<LeadTask> getListingMappedTasksByTaskIds(List<Integer> taskIds);
}