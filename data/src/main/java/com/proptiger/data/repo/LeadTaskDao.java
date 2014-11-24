package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.LeadTask.AgentOverDueTaskCount;
import com.proptiger.data.model.marketplace.TaskOfferedListingMapping;

/**
 * 
 * @author azi
 * 
 */
public interface LeadTaskDao extends JpaRepository<LeadTask, Integer> {
    public List<LeadTask> findByLeadOfferId(int leadOfferId);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN FETCH LT.leadOffer LO INNER JOIN FETCH LT.taskStatus LTS INNER JOIN FETCH LTS.masterLeadTask MLT INNER JOIN FETCH LTS.masterLeadTaskStatus MLTS LEFT JOIN FETCH LT.statusReason LTSR LEFT JOIN FETCH LT.offeredListingMappings TOLM LEFT JOIN FETCH TOLM.offeredListing WHERE LO.agentId = ?1 order by LT.scheduledFor desc")
    public List<LeadTask> getLeadTasksForUser(int userId, Pageable pageable);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN FETCH LT.leadOffer LO JOIN FETCH LT.taskStatus LTS INNER JOIN FETCH LTS.masterLeadTask MLT INNER JOIN FETCH LTS.masterLeadTaskStatus MLTS LEFT JOIN FETCH LT.statusReason LTSR LEFT JOIN FETCH LT.offeredListingMappings TOLM LEFT JOIN FETCH TOLM.offeredListing WHERE LT.id = ?1")
    public LeadTask getLeadTaskDetails(int taskId);

    @Query(value = "SELECT COUNT(LT) FROM LeadTask LT INNER JOIN LT.leadOffer LO WHERE LO.agentId = ?1")
    public long getLeadTaskCountForUser(int userId);

    @Query(
            value = "SELECT TOLM from LeadTask LT JOIN LT.offeredListingMappings TOLM INNER JOIN FETCH TOLM.offeredListing LOL WHERE TOLM.taskId = ?1 order by TOLM.createdAt desc")
    public List<TaskOfferedListingMapping> getMappedListingMappingsForTask(int taskId);

    @Query(
            value = "SELECT DISTINCT(TOLM) FROM TaskOfferedListingMapping TOLM join fetch TOLM.offeredListing LOL join fetch LOL.listing LI left join fetch LI.projectSupply LIPS left join fetch LI.currentListingPrice join fetch LI.property LIP join fetch LIP.project LIPP join fetch LIPP.projectStatusMaster join fetch LIPP.builder join fetch LIPP.locality LIPPL join fetch LIPPL.suburb LIPPLS join fetch LIPPLS.city where LIPP.version = 'Website' and (LIPS.version is null or LIPS.version = 'Website') and LI.status = 'Active' and TOLM.taskId in (?1)  order by TOLM.createdAt desc")
    public List<TaskOfferedListingMapping> getTaskOfferedListingMappings(List<Integer> taskIds);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN FETCH LT.offeredListingMappings TOLM INNER JOIN FETCH TOLM.offeredListing where LT.id IN (?1)")
    public List<LeadTask> getListingMappedTasksByTaskIds(List<Integer> taskIds);

    @Query("select LT from LeadTask LT JOIN FETCH LT.taskStatus LTS LEFT JOIN FETCH LTS.resultingStatus INNER JOIN FETCH LTS.masterLeadTask MLT INNER JOIN FETCH LTS.masterLeadTaskStatus MLTS LEFT JOIN FETCH LT.statusReason WHERE LT.leadOfferId in (?1) order by LT.performedAt desc")
    public List<LeadTask> findTasksByLeadOfferId(int leadOfferId);

    @Query("select LT from LeadTask LT JOIN FETCH LT.taskStatus LTS LEFT JOIN FETCH LTS.resultingStatus INNER JOIN FETCH LTS.masterLeadTask MLT INNER JOIN FETCH LTS.masterLeadTaskStatus MLTS LEFT JOIN FETCH LT.statusReason WHERE LT.id in (?1) order by LT.performedAt desc")
    public List<LeadTask> findById(List<Integer> leadTaskIds);

    @Query("select LT from LeadTask LT JOIN FETCH LT.leadOffer LO JOIN FETCH LO.lead L WHERE LT.id in (?1)")
    public List<LeadTask> findByIdUptoLead(List<Integer> leadTaskIds);

    @Query(
            value = "SELECT LT FROM LeadTask LT INNER JOIN LT.statusReason TSR WHERE LT.leadOfferId = ?1 AND TSR.reason = ?2")
    public List<LeadTask> findByofferIdAndStatusReason(int offerId, String statusReason);

    @Query(
            value = "SELECT NEW com.proptiger.data.model.marketplace.LeadTask$AgentOverDueTaskCount(LO.agentId, COUNT(*)) from LeadOffer LO JOIN LO.nextTask NT WHERE NT.scheduledFor < ?1 GROUP by LO.agentId HAVING COUNT(*) >= ?2")
    public List<AgentOverDueTaskCount> findOverDueTasksForAgents(Date scheduledForBefore, long overDueTaskCount);
}
