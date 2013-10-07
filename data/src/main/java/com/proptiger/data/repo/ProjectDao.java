/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;

/**
 *
 * @author mukand
 */
@Repository
public class ProjectDao extends ProjectSolrDao {
        @Autowired
        private ProjectDBDao projectDBDao;
        
        public ProjectDB findByProjectId(int projectId){
            return projectDBDao.findByProjectId(projectId);
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
