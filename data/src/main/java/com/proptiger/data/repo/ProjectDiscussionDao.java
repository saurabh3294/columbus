package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDiscussion;

@Repository
public interface ProjectDiscussionDao extends PagingAndSortingRepository<Project, Integer> {
    public Project findByProjectIdAndVersion(int projectId, DataVersion dataVersion);

    @Query("SELECT pd " + "FROM ProjectDiscussion pd "
            + "WHERE pd.projectId = ?1 "
            + "AND pd.status = '1' "
            + "AND pd.user.status = '1' "
            + "ORDER BY pd.id DESC")
    public List<ProjectDiscussion> getProjectDiscussions(int projectId);

    @Query("SELECT pd FROM ProjectDiscussion pd WHERE pd.parentId = ?1")
    public List<ProjectDiscussion> getChildrenProjectDiscussions(Integer commentId);

    @Query("SELECT p.projectName FROM ProjectDB p WHERE p.version = 'Website' AND p.projectId = ?1")
    public String getProjectNameById(Integer projectId);

    @Query("SELECT pd.projectId FROM Project P , ProjectDiscussion pd " + "WHERE pd.projectId=p.projectId AND p.version='Website' AND pd.createdDate >= ?1"
            + " AND CASE ?2 WHEN 1 THEN p.locality.suburb.city.id  WHEN 2 THEN p.locality.suburb.id WHEN 3 THEN p.localityId END = ?3 "
            + " GROUP BY pd.projectId HAVING COUNT(*) > ?4 ORDER BY pd.createdDate DESC, COUNT(*) DESC , p.assignedPriority ASC")
    public List<Integer> getRecentlyMostDiscussedProjects(
            @Param Date date,
            @Param int localityType,
            @Param int cityId,
            @Param long minCount);

    @Query("SELECT pd.projectId FROM Project P , ProjectDiscussion pd " + "WHERE pd.projectId=p.projectId AND p.version='Website' AND pd.createdDate >= ?1"
            + " AND CASE ?2 WHEN 1 THEN p.locality.suburb.city.id  WHEN 2 THEN p.locality.suburb.id WHEN 3 THEN p.localityId END = ?3 "
            + " GROUP BY pd.projectId HAVING COUNT(*) > ?4 ORDER BY COUNT(*) DESC , p.assignedPriority ASC")
    public List<Integer> getMostDiscussedProjects(
            @Param Date date,
            @Param int localityType,
            @Param int cityId,
            @Param long minCount);
    
    @Query("SELECT P FROM Project P left join fetch P.projectStatusMaster PM left join fetch P.builder B left join fetch P.locality L left join fetch L.suburb S left join fetch S.city C where P.projectId = ?1 AND P.version = 'Website' ")
    public Project getProjectOnId(Integer projectId);
    
    @Query("SELECT P FROM Project P left join fetch P.builder B where P.projectId IN ?1 AND P.version = 'Website' ")
    public List<Project> getProjectsOnId(List<Integer> projectId);
    
}
