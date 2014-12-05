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

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.model.cms.ProjectDB;
import com.proptiger.data.internal.dto.GenericKeyValue;

/**
 * 
 * @author mukand
 */
@Repository
public interface ProjectDBDao extends PagingAndSortingRepository<ProjectDB, Serializable> {
    public ProjectDB findByProjectIdAndVersion(int projectId, DataVersion dataVersion);

    @Query("SELECT new com.proptiger.data.internal.dto.GenericKeyValue(p.projectId,p.projectName) FROM ProjectDB p WHERE p.version = 'Website' AND p.projectId in ?1")
    public List<GenericKeyValue> getProjectNameById(List<Integer> projectIds);
}
