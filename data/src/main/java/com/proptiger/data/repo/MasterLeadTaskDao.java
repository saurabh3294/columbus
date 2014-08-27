package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.MasterLeadTask;

public interface MasterLeadTaskDao extends JpaRepository<MasterLeadTask, Integer> {
    @Query(
            value = "SELECT MLT FROM LeadTask LT INNER JOIN LT.taskStatus LTS INNER JOIN LTS.masterLeadTaskStatus MLTS INNER JOIN LTS.masterLeadTask MLT WHERE LT.leadOfferId = ?1 AND MLTS.complete = true AND MLT.optional = false GROUP BY MLT.id")
    public List<MasterLeadTask> getcompleteMandatoryTasks(int leadOfferId);

    public List<MasterLeadTask> findByPriorityLessThanAndOptional(int priority, boolean optional);

    @Query(
            value = "SELECT DISTINCT MLT FROM MasterLeadTask MLT LEFT JOIN FETCH MLT.leadTaskStatuses LTS LEFT JOIN FETCH LTS.masterLeadTaskStatus MLTS")
    public List<MasterLeadTask> getMasterTaskDetails();
}