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
public interface ProjectDBDao extends PagingAndSortingRepository<ProjectDB, Serializable> {
    public ProjectDB findByProjectIdAndVersion(int projectId, DataVersion dataVersion);

    @Query("SELECT pd " + "FROM ProjectDiscussion pd JOIN Fetch pd.user "
            + "WHERE pd.projectId = ?1 "
            + "AND pd.status = '1' "
            + "AND pd.user.status = '1' "
            + "ORDER BY pd.id DESC")
    public List<ProjectDiscussion> getProjectDiscussions(int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd JOIN FETCH pd.user WHERE pd.parentId = ?1")
    public List<ProjectDiscussion> getChildrenProjectDiscussions(Long commentId);

    @Query("SELECT p.projectName FROM ProjectDB p WHERE p.version = 'Website' AND p.projectId = ?1")
    public String getProjectNameById(Integer projectId);
}
