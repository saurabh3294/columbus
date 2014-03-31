/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.TableAttributes;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectSolrDao;
import com.proptiger.data.repo.TableAttributesDao;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.util.IdConverterForDatabase;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.data.util.UtilityClass;
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
    private ProjectDao             projectDao;

    @Autowired
    private ImageEnricher          imageEnricher;

    @Autowired
    private PropertyService        propertyService;

    @Autowired
    private MailSender             mailSender;

    @Autowired
    private LocalityAmenityService localityAmenityService;

    @Autowired
    private TableAttributesDao     tableAttributesDao;

    @Autowired
    private LocalityService        localityService;

    @Autowired
    private BuilderService         builderService;

    @Autowired
    private ProjectAmenityService  projectAmenityService;

    @Autowired
    private VideoLinksService      videoLinksService;

    @Autowired
    private BankService            bankService;

    @Autowired
    private TableAttributesService tableAttributesService;
    
    @Autowired
    private ProjectSolrDao projectSolrDao;

    /**
     * This method will return the list of projects and total projects found
     * based on the selector.
     * 
     * @param projectFilter
     * @return SolrServiceResponse<List<Project>> it will contain the list of
     *         localities and total projects found.
     */
    public PaginatedResponse<List<Project>> getProjects(Selector projectFilter) {
        PaginatedResponse<List<Project>> projects = projectDao.getProjects(projectFilter);
        imageEnricher.setProjectsImages(projects.getResults());

        return projects;
    }

    /**
     * Returns projects ordered by launch date (descending)
     * 
     * @param cityName
     * @param projectFilter
     * @return
     */
    public PaginatedResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector projectFilter) {
        return projectDao.getNewProjectsByLaunchDate(cityName, projectFilter);
    }

    /**
     * Returns projects with status 'Pre Launch' and 'Not Launched'.
     * 
     * @param cityName
     * @param projectFilter
     * @return
     */
    public PaginatedResponse<List<Project>> getUpcomingNewProjects(String cityName, Selector projectFilter) {
        return projectDao.getUpcomingNewProjects(cityName, projectFilter);
    }

    /**
     * Returns specifications of a project
     * 
     * @param projectId
     * @return
     */
    public ProjectSpecification getProjectSpecifications(int projectId) {
        return null;// projectSpecificationDao.findById(projectId);
    }

    /**
     * Returns all details of a project
     * 
     * @param projectId
     * @return
     */
    public ProjectDB getProjectDetails(Integer projectId) {
        ProjectDB project = projectDao.findByProjectId(projectId);
        if (project == null) {
            throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }
        imageEnricher.setProjectDBImages(project);
        return project;
    }

    /**
     * Returns all details of a project on the Project Model Object.
     * 
     * @param projectId
     * @return Project Model Object
     */
    public Project getProjectInfoDetails(Selector selector, Integer projectId) {

        List<Project> solrProjects = getProjectsByIds(new HashSet<Integer>(Arrays.asList(projectId)));
        if (solrProjects == null || solrProjects.size() < 1)
            return null;
        Project project = solrProjects.get(0);

        List<Property> properties = propertyService.getProperties(projectId);
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            Double pricePerUnitArea = property.getPricePerUnitArea();

            if (pricePerUnitArea == null)
                pricePerUnitArea = 0D;

            // set Primary Prices.
            project.setMinPricePerUnitArea(UtilityClass.min(pricePerUnitArea, project.getMinPricePerUnitArea()));
            project.setMaxPricePerUnitArea(UtilityClass.max(pricePerUnitArea, project.getMaxPricePerUnitArea()));
            // setting distinct bedrooms
            project.addBedrooms(property.getBedrooms());
            project.addPropertyUnitType(property.getUnitType());

            // setting resale Price
            Double resalePrice = property.getResalePrice();
            project.setMaxResalePrice(UtilityClass.max(resalePrice, project.getMaxResalePrice()));
            project.setMinResalePrice(UtilityClass.min(resalePrice, project.getMinResalePrice()));
            project.setResale(property.getProject().isIsResale() | project.isIsResale());

            property.setProject(null);
        }

        Set<String> fields = selector.getFields();

        /*
         * Setting properites if needed.
         */
        if (fields == null || fields.contains("properties")) {
            project.setProperties(properties);
            imageEnricher.setPropertiesImages(properties);
        }

        /*
         * Setting neighborhood if needed.
         */
        if (fields == null || fields.contains("neighborhood")) {
            project.setNeighborhood(localityAmenityService.getLocalityAmenities(project.getLocalityId(), null));
        }

        /*
         * Setting project Specification if needed.
         */
        if (fields == null || fields.contains("projectSpecification")) {
            project.setProjectSpecification(this.getProjectSpecificationsV2(projectId));
        }

        /*
         * Setting builders if needed.
         */
        if (fields == null || fields.contains("builder")) {
            project.setBuilder(builderService.getBuilderInfo(project.getBuilderId(), null));
        }

        /*
         * setting project amenities if needed.
         */
        if (fields == null || fields.contains("projectAmenities")) {
            project.setProjectAmenities(projectAmenityService.getCMSAmenitiesByProjectId(projectId));
        }

        /*
         * setting video links if needed.
         */
        if (fields == null || fields.contains("videoUrls")) {
            project.setVideoUrls(videoLinksService.getProjectVideoLinks(project.getProjectId()));
        }

        /*
         * setting Locality Object.
         */
        if (fields == null || fields.contains("locality")) {
            project.setLocality(localityService.getLocality(project.getLocalityId()));
            imageEnricher.setLocalityImages(project.getLocality(), null);
        }

        /*
         * Setting loan banks if needed.
         */
        if (fields == null || fields.contains("loanProviderBanks")) {
            List<Bank> bankList = bankService.getBanksProvidingLoanOnProject(projectId);
            project.setLoanProviderBanks(bankList);
        }

        /*
         * setting images.
         */
        imageEnricher.setProjectImages(project);

        /*
         * Setting locality Ratings And Reviews
         */
        localityService.updateLocalityRatingAndReviewDetails(project.getLocality());

        return project;
    }

    /**
     * Returns all discussions for a project
     * 
     * @param projectId
     * @param commentId
     * @return
     */
    public List<ProjectDiscussion> getDiscussions(int projectId, Long commentId) {
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
    public List<Project> getPopularProjects(Selector projectSelector) {
        LinkedHashSet<SortBy> sortBySet = createdSortingForPopularProjects();
        // sorting provided in api call will not be considered
        projectSelector.setSort(sortBySet);
        PaginatedResponse<List<Project>> result = getProjects(projectSelector);
        return result.getResults();
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

        // first sorting by enquiry count
        sortBySet.add(sortByEnquiryCount);
        // second sorting by assigned priority
        sortBySet.add(sortByAssignedPriority);
        // third sorting by computed priority
        sortBySet.add(sortByComputedPriority);
        // fourth sorting by project id
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
    public boolean sendProjectDetailsMail(String to, Integer projectId) {
        boolean sent = false;
        Set<Integer> projectIdSet = new HashSet<Integer>();
        projectIdSet.add(projectId);
        List<Project> projects = getProjectsByIds(projectIdSet);
        if (projects != null && projects.size() >= 1) {
            Project project = projects.get(0);
            // TODO waiting for html template
            sent = mailSender.sendMailUsingAws(
                    to,
                    null,
                    null,
                    "test mail content for ptoject id=" + projectId,
                    "test subject for ptoject id=" + projectId);
        }
        else {
            throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }
        return sent;
    }

    /**
     * Get projects by project ids
     * 
     * @param ids
     * @return
     */
    public List<Project> getProjectsByIds(Set<Integer> ids) {
        List<SolrResult> result = projectDao.getProjectsOnIds(ids);
        List<Project> projects = new ArrayList<Project>();
        if (result != null) {
            for (SolrResult solrResult : result) {
                projects.add(solrResult.getProject());
            }
        }
        return projects;
    }

    /**
     * This method will return the total number of project discussions in the
     * project.
     * 
     * @param projectId
     * @return total project discussions.
     */
    @Deprecated
    private Integer getTotalProjectDiscussionCount(int projectId) {

        Integer totalProjectDiscussion = 0;
        List<ProjectDiscussion> projectDiscussionList = getDiscussions(projectId, null);
        if (projectDiscussionList != null)
            totalProjectDiscussion = projectDiscussionList.size();

        return totalProjectDiscussion;
    }

    /**
     * This method will return the project specifications From CMS by new
     * database for a project. architecture.
     * 
     * @param projectId
     * @return
     */
    public ProjectSpecification getProjectSpecificationsV2(int projectId) {
        int cmsProjectId = IdConverterForDatabase.getCMSDomainIdForDomainTypes(DomainObject.project, projectId);
        List<TableAttributes> specifications = tableAttributesService.getTableAttributes(cmsProjectId, "resi_project");

        return new ProjectSpecification(specifications);
    }

    /**
     * This Method will return the Most Recently Discussed Projects in a city or
     * suburb or locality.
     * 
     * @param locationTypeStr
     * @param locationId
     * @param lastNumberOfWeeks
     * @param minProjectDiscussionCount
     * @return List of recently discussed projects.
     */
    public List<Project> getMostRecentlyDiscussedProjects(
            String locationTypeStr,
            int locationId,
            int lastNumberOfWeeks,
            int minProjectDiscussionCount) {

        int numberOfDays = lastNumberOfWeeks * 7 * -1;
        Calendar cal = Calendar.getInstance();// intialize your date to any date
        cal.add(Calendar.DATE, numberOfDays);

        int locationType;
        switch (locationTypeStr) {
            case "city":
                locationType = 1;
                break;
            case "suburb":
                locationType = 2;
                break;
            case "locality":
                locationType = 3;
                break;
            default:
                throw new IllegalArgumentException("The possbile values are : suburb or locality or city.");
        }
        List<Integer> projectIds = projectDao.getMostRecentlyDiscussedProjectInNWeeksOnLocation(
                cal.getTime(),
                locationType,
                locationId,
                minProjectDiscussionCount);

        if (projectIds == null || projectIds.size() < 1)
            return null;

        return getProjectsByIds(new HashSet<Integer>(projectIds));
    }

    /**
     * This method will return the most discussed projects in a city or suburb
     * or locality.
     * 
     * @param locationTypeStr
     * @param locationId
     * @param lastNumberOfWeeks
     * @param minProjectDiscussionCount
     * @return List of Most discussed Projects.
     */
    public List<Project> getMostDiscussedProjects(
            String locationTypeStr,
            int locationId,
            int lastNumberOfWeeks,
            int minProjectDiscussionCount) {

        int numberOfDays = lastNumberOfWeeks * 7 * -1;
        Calendar cal = Calendar.getInstance();// intialize your date to any date
        cal.add(Calendar.DATE, numberOfDays);

        int locationType;
        switch (locationTypeStr) {
            case "city":
                locationType = 1;
                break;
            case "suburb":
                locationType = 2;
                break;
            case "locality":
                locationType = 3;
                break;
            default:
                throw new IllegalArgumentException("The possbile values are : suburb or locality or city.");
        }
        List<Integer> projectIds = projectDao.getMostDiscussedProjectInNWeeksOnLocation(
                cal.getTime(),
                locationType,
                locationId,
                minProjectDiscussionCount);

        if (projectIds == null || projectIds.size() < 1)
            return null;

        return getProjectsByIds(new HashSet<Integer>(projectIds));
    }

    public PaginatedResponse<List<Project>> getProjects(FIQLSelector selector) {
        return projectDao.getProjects(selector);
    }

    public Project getProjectData(int projectId) {
        Set<Integer> projectIds = new HashSet<>();
        projectIds.add(projectId);

        List<Project> projects = getProjectsByIds(projectIds);

        if (projects != null && projects.size() > 0)
            return projects.get(0);

        throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
    }

    public PaginatedResponse<List<Project>> getHighestReturnProjects(
            String locationType,
            int locationId,
            int numberOfProjects,
            double minimumPriceRise)
    {
        String json = "{\"paging\":{\"rows\":" + numberOfProjects
                + "},\"filters\":{\"and\":[{\"equal\":{\""
                + locationType
                + "Id\":"
                + locationId
                + "}},{\"range\":{\"projectAvgPriceRiseMonths\":{\"from\":1},\"projectAvgPriceRisePercentage\":{\"from\":"
                + minimumPriceRise
                + "}}}]},\"sort\":[{\"field\":\"projectPriceAppreciationRate\",\"sortOrder\":\"DESC\"}]}";

        Selector selector = new Gson().fromJson(json, Selector.class);
        return projectDao.getProjects(selector);
    }
    
    /**
     * Get project count by status in map for a given selector
     * @param selector
     * @return
     */
    public Map<String, Long> getProjectStatusCount(Selector selector){
        return projectSolrDao.getProjectStatusCount(selector);
    }
}
