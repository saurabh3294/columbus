/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectSpecificationDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectSpecificationDao projectSpecificationDao;
    
    @Autowired
    private ImageEnricher imageEnricher;
    
    @Autowired
    private PropertyService propertyService;

    public SolrServiceResponse<List<Project>> getProjects(Selector projectFilter) {
    	SolrServiceResponse<List<Project>> projects =  projectDao.getProjects(projectFilter);
    	imageEnricher.setProjectsImages("main", projects.getResult(), null);
    	
    	return projects;
    }

    /**
     * Returns projects ordered by launch date (descending)
     *
     * @param cityName
     * @param projectFilter
     * @return
     */
    public SolrServiceResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector projectFilter) {
        return projectDao.getNewProjectsByLaunchDate(cityName, projectFilter);
    }

    /**
     * Returns projects with status 'Pre Launch' and 'Not Launched'.
     *
     * @param cityName
     * @param projectFilter
     * @return
     */
    public SolrServiceResponse<List<Project>> getUpcomingNewProjects(String cityName, Selector projectFilter) {
        return projectDao.getUpcomingNewProjects(cityName, projectFilter);
    }

    /**
     * Returns specifications of a project
     *
     * @param projectId
     * @return
     */
    public ProjectSpecification getProjectSpecifications(int projectId) {
        return projectSpecificationDao.findById(projectId);
    }

    /**
     * Returns all details of a project
     * @param projectId
     * @return
     */
    public ProjectDB getProjectDetails(Integer projectId) {
        ProjectDB project = projectDao.findByProjectId(projectId);
        imageEnricher.setProjectDBImages(null, project);
        if (project == null) {
            throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }
        return project;
    }
    
    /**
     * Returns all details of a project on the Project Model Object.
     * @param projectId
     * @return Project Model Object
     */
    public Project getProjectInfoDetails(Selector propertySelector, Integer projectId){
    	Project project  = projectDao.findProjectByProjectId(projectId);
    	List<Property> properties = propertyService.getProperties(projectId);
    	for(int i=0; i<properties.size(); i++)
    		properties.get(i).setProject(null);
    	
    	project.setProperties(properties);
    	project.setTotalProjectDiscussion(getTotalProjectDiscussionCount(projectId));
    	imageEnricher.setProjectImages(null, project, null);
    	
    	return project;
    }
    
    /**
     * Returns all discussions for a project
     *
     * @param projectId
     * @param commentId
     * @return
     */
    public List<ProjectDiscussion> getDiscussions(int projectId, Integer commentId) {
        List<ProjectDiscussion> discussions = projectDao.getDiscussions(projectId, commentId);
        for (ProjectDiscussion projectDiscussion : discussions) {
            if ("proptiger".equals(projectDiscussion.getUser().getUsername())) {
                projectDiscussion.getUser().setUsername(projectDiscussion.getAdminUserName());
            }
        }

        return discussions;
    }
    
	/**
	 * This methods get popular projects for city/locality id provided in
	 * filter. Popular will be selected based on number of queries in last π
	 * week, in case of tie use assigned priority and dynamic priority part of
	 * selector object
	 * 
	 * π = 8 weeks that is configured in solr while inserting enquiry count
	 * 
	 * @param projectSelector
	 * 
	 * @return
	 */
    public List<Project> getPopularProjects(Selector projectSelector){
    	LinkedHashSet<SortBy> sortBySet = createdSortingForPopularProjects();
    	projectSelector.setSort(sortBySet);
    	SolrServiceResponse<List<Project>> result = getProjects(projectSelector);
    	return result.getResult();
    }

	/**
	 * Creating sorting part of selector object to get popular projects
	 * 
	 * @return
	 */
	private LinkedHashSet<SortBy> createdSortingForPopularProjects() {
		LinkedHashSet<SortBy> sortBySet = new LinkedHashSet<SortBy>();
    	SortBy sortByEnquiryCount = new SortBy();
    	sortByEnquiryCount.setField("projectEnquiryCount");
    	sortByEnquiryCount.setSortOrder(SortOrder.DESC);
    	
    	SortBy sortByAssignedPriority = new SortBy();
    	sortByAssignedPriority.setField("assignedPriority");
    	sortByAssignedPriority.setSortOrder(SortOrder.ASC);
    	
    	SortBy sortByComputedPriority = new SortBy();
    	sortByComputedPriority.setField("computedPriority");
    	sortByComputedPriority.setSortOrder(SortOrder.ASC);
    	
    	SortBy sortByProjectId = new SortBy();
    	sortByProjectId.setField("projectId");
    	sortByProjectId.setSortOrder(SortOrder.ASC);
    	
    	//first sorting by enquiry count 
    	sortBySet.add(sortByEnquiryCount);
    	//second sorting by assigned priority
    	sortBySet.add(sortByAssignedPriority);
    	//third sorting by computed priority
    	sortBySet.add(sortByComputedPriority);
    	//fourth sorth by project id
    	sortBySet.add(sortByProjectId);
		return sortBySet;
	}
	
	private Integer getTotalProjectDiscussionCount(int projectId){
		
		Integer totalProjectDiscussion = 0;
		List<ProjectDiscussion> projectDiscussionList = getDiscussions(projectId, null);
        if(projectDiscussionList!=null)
        	totalProjectDiscussion = projectDiscussionList.size();
        
        return totalProjectDiscussion;
	}
}
