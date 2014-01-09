/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.TableAttributes;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.TableAttributesDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.data.util.IdConverterForDatabase;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;
import com.proptiger.mail.service.MailSender;

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
    private ImageEnricher imageEnricher;
    
    @Autowired
    private PropertyService propertyService;

 	@Autowired
	private MailSender mailSender;
 	
 	@Autowired
 	private LocalityAmenityService localityAmenityService;
 	
 	@Autowired
 	private TableAttributesDao tableAttributesDao;

    /**
     * This method will return the list of projects and total projects found based on the selector.
     * @param projectFilter
     * @return SolrServiceResponse<List<Project>> it will contain the list of localities and
     *         total projects found.
     */
    public SolrServiceResponse<List<Project>> getProjects(Selector projectFilter) {
    	SolrServiceResponse<List<Project>> projects =  projectDao.getProjects(projectFilter);
    	imageEnricher.setProjectsImages(projects.getResult());
    	
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
        return null;//projectSpecificationDao.findById(projectId);
    }

    /**
     * Returns all details of a project
     * @param projectId
     * @return
     */
    public ProjectDB getProjectDetails(Integer projectId) {
        ProjectDB project = projectDao.findByProjectId(projectId);
        imageEnricher.setProjectDBImages(project);
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
    	if(project == null)
    		return null;
    	
    	List<Property> properties = propertyService.getProperties(projectId);
    	for(int i=0; i<properties.size(); i++)
    		properties.get(i).setProject(null);
    	
    	project.setProperties(properties);
    	project.setTotalProjectDiscussion(getTotalProjectDiscussionCount(projectId));
    	project.setNeighborhood( localityAmenityService.getLocalityAmenities(project.getLocality().getLocalityId(), null) );
    	imageEnricher.setProjectImages(project);
    	project.setProjectSpecification( getProjectSpecificationsV2(projectId) );
    	
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
    	//sorting provided in api call will not be considered
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
    	//fourth sorting by project id
    	sortBySet.add(sortByProjectId);
		return sortBySet;
	}
	
	/**
	 * Get project details and send required project details to provided mail id
	 * 
	 * @param to
	 * @param projectId
	 * @return
	 */
	public boolean sendProjectDetailsMail(String to, Integer projectId){
		boolean sent = false;
		Set<Integer> projectIdSet = new HashSet<Integer>();
		projectIdSet.add(projectId);
		List<Project> projects = getProjectsByIds(projectIdSet);
		if (projects != null && projects.size() >= 1) {
			Project project = projects.get(0);
			// TODO waiting for html template
			sent = mailSender.sendMailUsingAws(to,
					"test mail content for ptoject id=" + projectId,
					"test subject for ptoject id=" + projectId);
		}
		else{
			throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
		}
		return sent;
	}

/**
	 * Get projects by project ids
	 * @param ids
	 * @return
	 */
	public List<Project> getProjectsByIds(Set<Integer> ids){
		List<SolrResult> result = projectDao.getProjectsOnIds(ids);
		List<Project> projects = new ArrayList<Project>();
		if(result != null){
			for(SolrResult solrResult: result){
				projects.add(solrResult.getProject());
			}
		}
		return projects;
	}

	private Integer getTotalProjectDiscussionCount(int projectId){
		
		Integer totalProjectDiscussion = 0;
		List<ProjectDiscussion> projectDiscussionList = getDiscussions(projectId, null);
        if(projectDiscussionList!=null)
        	totalProjectDiscussion = projectDiscussionList.size();
        
        return totalProjectDiscussion;
	}
	
	public ProjectSpecification getProjectSpecificationsV2(int projectId){
		
		int cmsProjectId = IdConverterForDatabase.getCMSDomainIdForDomainTypes("project", projectId);
		List<TableAttributes> specifications = tableAttributesDao.findByTableIdAndTableName(cmsProjectId, "resi_project");
		
		/*Map<String, Object> specification = new ProjectSpecification(specifications).getSpecifications(); 
		//projectSp
		Gson gson = new Gson();
		System.out.println(gson.toJson(specification));*/
		//return projectSpecification;
		return new ProjectSpecification(specifications);
	}
}
