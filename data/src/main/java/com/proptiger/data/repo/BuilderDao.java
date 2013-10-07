/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.Builder;

/**
 *
 * @author mukand
 */
public interface BuilderDao extends PagingAndSortingRepository<Builder, Serializable>{
    
    @Query("SELECT B FROM Builder B , ProjectDB P WHERE B.id=P.builderId"
            + " AND P.projectId=?1")
    public Builder findByProjectId(int projectId);
    
}
