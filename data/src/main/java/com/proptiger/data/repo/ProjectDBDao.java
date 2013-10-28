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

import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;

/**
 *
 * @author mukand
 */
@Repository
public interface ProjectDBDao extends PagingAndSortingRepository<ProjectDB, Serializable>{
    public ProjectDB findByProjectId(int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd JOIN FETCH pd.user WHERE pd.projectId = ?1")
    public List<ProjectDiscussion> getProjectDiscussions(int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd JOIN FETCH pd.user WHERE pd.parentId = ?1")
    public List<ProjectDiscussion> getChildrenProjectDiscussions(Integer commentId);
    
    @Query("SELECT p.projectName FROM ProjectDB p WHERE p.projectId = ?1")
    public String getProjectName(Integer projectId);
}
