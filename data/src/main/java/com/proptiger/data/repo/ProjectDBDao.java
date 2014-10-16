/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;

/**
 * 
 * @author mukand
 */
@Repository
public interface ProjectDBDao extends PagingAndSortingRepository<ProjectDB, Serializable> {
    public ProjectDB findByProjectIdAndVersion(int projectId, DataVersion dataVersion);

    @Query("SELECT p.projectName FROM ProjectDB p WHERE p.version = 'Website' AND p.projectId = ?1")
    public String getProjectNameById(Integer projectId);
}
