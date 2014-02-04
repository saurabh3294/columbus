/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 *
 * @author Mukand
 */
@Repository
public class ProjectDao extends ProjectSolrDao {
        @Autowired
        private ProjectDBDao projectDBDao;
        
        @Autowired
        private ProjectDatabaseDao projectDatabaseDao;
        
        @Autowired
        private EntityManagerFactory emf;

        public ProjectDB findByProjectId(int projectId){
            return projectDBDao.findByProjectId(projectId);
        }
        
        public Project findProjectByProjectId(int projectId){
        	return projectDatabaseDao.findByProjectId(projectId);
        }

        public List<ProjectDiscussion> getDiscussions(int projectId, Long commentId) {
            if (commentId == null) {
                return projectDBDao.getProjectDiscussions(projectId);
            }
            else {
                return projectDBDao.getChildrenProjectDiscussions(commentId);
            }
        }
        
        public List<Integer> getMostRecentlyDiscussedProjectInNWeeksOnLocation(Date date, int locationType, int locationId, int minCount){
        	return projectDatabaseDao.getRecentlyMostDiscussedProjects(date, locationType, locationId, minCount);
        }
        
        public List<Integer> getMostDiscussedProjectInNWeeksOnLocation(Date date, int locationType, int locationId, int minCount){
        	return projectDatabaseDao.getMostDiscussedProjects(date, locationType, locationId, minCount);
        }

        public PaginatedResponse<List<Project>> getProjects(FIQLSelector selector) {
            AbstractQueryBuilder<Project> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Project.class);
            builder.buildQuery(selector);
            PaginatedResponse<List<Project>> paginatedResponse = new PaginatedResponse<>();
            paginatedResponse.setResults(builder.retrieveResults());
            paginatedResponse.setTotalCount(builder.retrieveCount());
            return paginatedResponse;
        }
}
