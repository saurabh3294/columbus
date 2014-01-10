package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDiscussion;

@Repository
public interface ProjectDatabaseDao extends PagingAndSortingRepository<Project, Integer>{
	public Project findByProjectId(int projectId);

    @Query("SELECT pd " +
    	   "FROM ProjectDiscussion pd " +
    	   "WHERE pd.projectId = ?1 " +
    	   "AND pd.status = '1' " +
    	   "AND pd.user.status = '1' " +
    	   "ORDER BY pd.id DESC")
    public List<ProjectDiscussion> getProjectDiscussions(int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd WHERE pd.parentId = ?1")
    public List<ProjectDiscussion> getChildrenProjectDiscussions(Integer commentId);
    
    @Query("SELECT p.projectName FROM ProjectDB p WHERE p.projectId = ?1")
    public String getProjectNameById(Integer projectId);
}
