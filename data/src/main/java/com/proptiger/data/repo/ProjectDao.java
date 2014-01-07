/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;

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
        
        public ProjectDB findByProjectId(int projectId){
            return projectDBDao.findByProjectId(projectId);
        }
        
        public Project findProjectByProjectId(int projectId){
        	return projectDatabaseDao.findById(projectId);
        }

        public List<ProjectDiscussion> getDiscussions(int projectId, Integer commentId) {
            if (commentId == null) {
                return projectDBDao.getProjectDiscussions(projectId);
            }
            else {
                return projectDBDao.getChildrenProjectDiscussions(commentId);
            }
        }
}
