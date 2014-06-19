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

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.enums.SortOrder;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.internal.dto.SenderDetail;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.TableAttributes;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectSolrDao;
import com.proptiger.data.repo.TableAttributesDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.IdConverterForDatabase;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao              projectDao;

    @Autowired
    private ImageEnricher           imageEnricher;

    @Autowired
    private PropertyService         propertyService;

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private LandMarkService         localityAmenityService;

    @Autowired
    private TableAttributesDao      tableAttributesDao;

    @Autowired
    private LocalityService         localityService;

    @Autowired
    private BuilderService          builderService;

    @Autowired
    private ProjectAmenityService   projectAmenityService;

    @Autowired
    private VideoLinksService       videoLinksService;

    @Autowired
    private BankService             bankService;

    @Autowired
    private TableAttributesService  tableAttributesService;

    @Autowired
    private ProjectSolrDao          projectSolrDao;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Value("${proptiger.url}")
    private String websiteHost;
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

        return project;
    }

    /**
     * Returns all details of a project on the Project Model Object.
     * 
     * @param projectId
     * @return Project Model Object
     */
    @Cacheable(value = Constants.CacheName.PROJECT_DETAILS, key = "#projectId+':'+#selector")
    public Project getProjectInfoDetails(Selector selector, Integer projectId) {

        List<Project> solrProjects = getProjectsByIds(new HashSet<Integer>(Arrays.asList(projectId)));
        if (solrProjects == null || solrProjects.size() < 1) {
            throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }

        Project project = solrProjects.get(0);

        List<Property> properties = propertyService.getPropertiesForProject(projectId);
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

        project.setMinResaleOrPrimaryPrice(UtilityClass.min(project.getMinPrice(), project.getMinResalePrice()));
        project.setMaxResaleOrPrimaryPrice(UtilityClass.max(project.getMaxPrice(), project.getMaxResalePrice()));

        Set<String> fields = selector.getFields();

        /*
         * Setting properites if needed.
         */
        if (fields == null || fields.contains("properties")) {
            project.setProperties(properties);
        }

        /*
         * Setting neighborhood if needed.
         */
        if (fields == null || fields.contains("neighborhood")) {
            project.setNeighborhood(localityAmenityService.getLandMarksForProject(project, null, null));
        }

        /*
         * Setting project Specification if needed.
         */
        if (fields == null || fields.contains("specifications")) {
            project.setProjectSpecification(this.getProjectSpecificationsV3(projectId));
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
    public boolean sendProjectDetailsMail(Integer projectId, SenderDetail senderDetails) {
        validateSenderDetails(senderDetails);
        Project project = getProjectData(projectId);
        ProjectDetailMailContent content = new ProjectDetailMailContent(
                senderDetails.getSenderName(),
                websiteHost + project.getURL(),
                senderDetails.getMessage());
        MailBody mailBody = mailBodyGenerator
                .generateMailBody(MailTemplateDetail.PROJECT_DETAILS_MAIL_TO_USER, content);

        MailDetails mailDetailsModified = new MailDetails(mailBody).setMailTo(senderDetails.getMailTo()).setMailCC(
                senderDetails.getMailCC());
        return mailSender.sendMailUsingAws(mailDetailsModified);
    }

    /**
     * Validating emails, and applying check if sender and recipient address are
     * same or not.
     * 
     * @param mailDetails
     */
    private void validateSenderDetails(SenderDetail mailDetails) {
        EmailValidator emailValiDator = EmailValidator.getInstance();
        if (!emailValiDator.isValid(mailDetails.getSenderEmail()) || !emailValiDator.isValid(mailDetails.getMailTo())) {
            throw new ProAPIException(ResponseCodes.BAD_REQUEST, "Invalid email id");
        }
        if (mailDetails.getSenderEmail().equals(mailDetails.getMailTo())) {
            throw new ProAPIException(ResponseCodes.BAD_REQUEST, "Sender and recipient emails are same");
        }
        if (mailDetails.getSenderName() == null || mailDetails.getSenderName().isEmpty()) {
            throw new ProAPIException(ResponseCodes.BAD_REQUEST, "Sender name can not be empty");
        }
    }

    public static class ProjectDetailMailContent {
        private String senderName;
        private String projectLink;
        private String message;

        public ProjectDetailMailContent(String senderName, String projectLink, String msg) {
            super();
            this.senderName = senderName;
            this.projectLink = projectLink;
            this.message = msg;
        }

        public String getSenderName() {
            return senderName;
        }

        public String getProjectLink() {
            return projectLink;
        }

        public String getMessage() {
            return message;
        }

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
     * This method will return the project specifications From CMS by new
     * database for a project. architecture.
     * 
     * @param projectId
     * @return
     */
    public ProjectSpecification getProjectSpecificationsV3(int projectId) {
        int cmsProjectId = IdConverterForDatabase.getCMSDomainIdForDomainTypes(DomainObject.project, projectId);
        List<TableAttributes> specifications = tableAttributesService.getTableAttributes(cmsProjectId, "resi_project");

        return new ProjectSpecification(specifications, false);
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
            double minimumPriceRise) {
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
     * 
     * @param selector
     * @return
     */
    public Map<String, Long> getProjectStatusCount(Selector selector) {
        return projectSolrDao.getProjectStatusCount(selector);
    }

    public Map<String, Integer> getProjectCountByCities(Integer builderId, Selector selector) {
        return projectSolrDao.getProjectCountByCities(builderId, selector);
    }
}
