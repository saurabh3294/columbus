/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * 
 * @author Mukand
 */
@Repository
public class ProjectDao extends ProjectSolrDao {
    @Autowired
    private ProjectDBDao         projectDBDao;

    @Autowired
    private ProjectDaoNew projectDaoNew;

    @Autowired
    private EntityManagerFactory emf;
    
    @Deprecated
    public ProjectDB findByProjectId(int projectId) {
        return projectDBDao.findByProjectIdAndVersion(projectId, DataVersion.Website);
    }

    @Deprecated
    public Project findProjectByProjectId(int projectId) {
        return findActiveOrInactiveProjectById(projectId);
    }

    public List<Integer> getMostRecentlyDiscussedProjectInNWeeksOnLocation(
            Date date,
            int locationType,
            int locationId,
            int minCount) {
        return projectDaoNew.getRecentlyMostDiscussedProjects(date, locationType, locationId, minCount);
    }

    public List<Integer> getMostDiscussedProjectInNWeeksOnLocation(
            Date date,
            int locationType,
            int locationId,
            int minCount) {
        return projectDaoNew.getMostDiscussedProjects(date, locationType, locationId, minCount);
    }

    public PaginatedResponse<List<Project>> getProjects(FIQLSelector selector) {
        AbstractQueryBuilder<Project> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Project.class);
        builder.buildQuery(selector);
        PaginatedResponse<List<Project>> paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setResults(builder.retrieveResults());
        paginatedResponse.setTotalCount(builder.retrieveCount());
        return paginatedResponse;
    }

    public Integer getProjectIdForPropertyId(Integer propertyId) {
        EntityManager em = emf.createEntityManager();
        Query query = em
                .createNativeQuery("SELECT PROJECT_ID FROM proptiger.DELETED_RESI_PROJECT_TYPES WHERE TYPE_ID =" + propertyId
                        + " UNION SELECT PROJECT_ID FROM proptiger.RESI_PROJECT_TYPES WHERE TYPE_ID ="
                        + propertyId);
        List<Integer> projectIds = query.getResultList();
        em.close();
        if (projectIds != null && !projectIds.isEmpty()) {
            return projectIds.get(0);
        }
        return null;
    }

    public Project findActiveOrInactiveProjectById(Integer id) {
        return projectDaoNew.findByProjectIdAndVersion(id, DataVersion.Website);
    }
}