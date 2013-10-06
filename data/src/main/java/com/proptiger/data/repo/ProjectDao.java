/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ProjectDB;

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

}
