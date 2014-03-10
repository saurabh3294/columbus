package com.proptiger.data.repo.b2b;

import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.b2b.CatchmentProject;

public class CatchmentProjectDaoImpl {
    @Query("DELETE FROM CatchmentProject WHERE catchmentId = ?1 and projectId IN ?2")
    public void deleteBycatchmentIdAndProjectIdIn(Integer catchmentId, Integer[] projectIds){
    }
}
