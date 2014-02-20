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
    @Query("SELECT B FROM Builder B, ProjectDB P WHERE B.id = P.builderId" + " AND P.projectId=?1")
    public Builder findByProjectId(int projectId);
}
