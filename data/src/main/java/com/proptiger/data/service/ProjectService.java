/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSecondaryPrice;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectSecondaryPriceDao;
import com.proptiger.data.repo.ProjectSpecificationDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.data.util.IdConverterForDatabase;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
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
    
    @Autowired
    private ProjectSecondaryPriceDao projectSecondaryPriceDao;

    public SolrServiceResponse<List<Project>> getProjects(Selector projectFilter) {
        return projectDao.getProjects(projectFilter);
    }

    public SolrServiceResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector projectFilter) {
        return projectDao.getNewProjectsByLaunchDate(cityName, projectFilter);
    }

    public SolrServiceResponse<List<Project>> getUpcomingNewProjects(String cityName, Selector projectFilter) {
        return projectDao.getUpcomingNewProjects(cityName, projectFilter);
    }

    public ProjectSpecification getProjectSpecifications(int projectId) {
        return projectSpecificationDao.findById(projectId);
    }

    public ProjectDB getProjectDetails(Integer projectId) {
        ProjectDB project = projectDao.findByProjectId(projectId);
        if (project == null) {
        	throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }
        return project;
    }

    public List<ProjectDiscussion> getDiscussions(int projectId, Integer commentId) {
        List<ProjectDiscussion> discussions = projectDao.getDiscussions(projectId, commentId);
        for (ProjectDiscussion projectDiscussion : discussions) {
            if ("proptiger".equals(projectDiscussion.getUser().getUsername())) {
                projectDiscussion.getUser().setUsername(projectDiscussion.getAdminUserName());
            }
        }

        return discussions;
    }
    
    public Map<Integer, ProjectSecondaryPrice> getAllProjectPrices(){
    	List<ProjectSecondaryPrice> listProjectSecondaryPrices = projectSecondaryPriceDao.getLatestProjectPrices();
    	Map<Integer, ProjectSecondaryPrice> map = new HashMap<Integer, ProjectSecondaryPrice>();
    	
    	int len = listProjectSecondaryPrices.size();
    	ProjectSecondaryPrice projectSecondaryPrice;
    	int projectId;
    	for(int i=0; i<len; i++ ){
    		projectSecondaryPrice = listProjectSecondaryPrices.get(i);
    		projectId = IdConverterForDatabase.getNormalizedIdForDomainTypes("project", projectSecondaryPrice.getProjectId());
    		projectSecondaryPrice.setProjectId(projectId);
    		map.put(projectId, projectSecondaryPrice);
    	}
    	Gson gson = new Gson();
    	System.out.println(gson.toJson(map));
    	return map;
    }
    
    public ProjectSecondaryPrice getProjectSecondaryPriceByProjectId(int projectId){
    		int startId = DomainObject.project.getStartId();
    		Pageable pageable = new PageRequest(0, 1);
    		
    		List<ProjectSecondaryPrice> listProjectSecondaryPrice = projectSecondaryPriceDao.findByProjectIdOrderByIdDesc(projectId+startId, pageable);
    		if(listProjectSecondaryPrice.size() < 1)
    			return null;
    		
    		ProjectSecondaryPrice projectSecondaryPrice = listProjectSecondaryPrice.get(0);
    		int  normlizedProjectId = IdConverterForDatabase.getNormalizedIdForDomainTypes("project", projectSecondaryPrice.getProjectId());
    		projectSecondaryPrice.setProjectId(normlizedProjectId);
    		
    		return projectSecondaryPrice;
    }
}
