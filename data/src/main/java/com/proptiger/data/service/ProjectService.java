/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectSpecificationDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 *
 * @author mukand
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectSpecificationDao projectSpecificationDao;
    
    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);
    	
    public SolrServiceResponse<List<Project>>  getProjects(Selector projectFilter){
    	if (logger.isDebugEnabled()) {
			logger.debug("Get Projects, Request="+projectFilter);
		}
        return projectDao.getProjects(projectFilter);
    }
    
    public SolrServiceResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector projectFilter){
        if (logger.isDebugEnabled()) {
			logger.debug("Get Projects, Request="+projectFilter);
		}
        return projectDao.getNewProjectsByLaunchDate(cityName, projectFilter);
    }
    
    public ProjectSpecification getProjectSpecifications(int projectId){
       return projectSpecificationDao.findById(projectId);
    }
    
    public ProjectDB getProjectDetails(Integer projectId){
        ProjectDB project = projectDao.findByProjectId(projectId);
        if(project == null){
        	throw new ResourceNotAvailableException("Project#id#"+projectId+" not available");
        }
        return project;
    }

    public List<ProjectDiscussion> getDiscussions(int projectId, Integer commentId) {
        return projectDao.getDiscussions(projectId, commentId);
    }
}
