/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Builder;

/**
 * 
 * @author mukand
 */
@Repository
public interface BuilderDao extends PagingAndSortingRepository<Builder, Integer>, BuilderCustomDao {   
    
    @Query("SELECT ROUND(AVG(DATEDIFF(PROMISED_COMPLETION_DATE,LAUNCH_DATE)/30)) as avgCompletionTimeMonths FROM Project P WHERE P.version='Website' and P.builderId=?1")
    public Double getAvgCompletionTimeMonths(int builderId);
}
