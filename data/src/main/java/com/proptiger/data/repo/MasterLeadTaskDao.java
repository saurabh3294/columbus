package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.MasterLeadTask;

public interface MasterLeadTaskDao extends JpaRepository<MasterLeadTask, Integer> {
    @Query(
            value = "SELECT MLT FROM LeadTask LT INNER JOIN LT.taskStatus LTS INNER JOIN LTS.masterLeadTaskStatus MLTS RIGHT JOIN LTS.masterLeadTask MLT WHERE (LT.id IS NULL OR LT.leadOfferId = ?1) AND (MLTS.complete is null or MLTS.complete = false) AND MLT.priority < ?2 AND MLT.optional = false GROUP BY MLT.id")
    public List<MasterLeadTask> getIncompleteMandatoryTasksWithLesserPriority(int leadOfferId, int priority);
}
