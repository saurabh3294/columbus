/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import com.proptiger.data.model.CouponCatalogue;
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
    private String                  websiteHost;

    public Project getProjectDetail(int projectId) {
        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"projectId\":" + projectId
                + "}}]}}", Selector.class);

        List<Project> projects = projectDao.getProjects(selector).getResults();
        if (projects.size() == 0) {
            throw new ResourceNotAvailableException(ResourceType.PROJECT, ResourceTypeAction.GET);
        }
        else {
            return projects.get(0);
        }
    }

    @Autowired
    private MediaEnricher mediaEnricher;

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
        imageEnricher.setImagesOfProjects(projects.getResults());
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
    @SuppressWarnings("unchecked")
    public PaginatedResponse<List<Project>> getUpcomingNewProjects(String cityName, Selector projectFilter) {
        String cityClause = "";

        if (cityName != null && !cityName.isEmpty()) {
            cityClause = "{\"equal\":{\"cityLabel\":\"" + cityName + "\"}},";
        }

        projectFilter.setFilters(new Gson().fromJson("{\"and\":[" + cityClause
                + "{\"equal\":{\"projectStatus\":[\"pre launch\",\"not launched\"]}}]}", Map.class));
        return propertyService.getPropertiesGroupedToProjects(projectFilter);
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

        project.setMinResaleOrPrimaryPrice(UtilityClass.min(project.getMinPrice(), project.getMinResalePrice()));
        project.setMaxResaleOrPrimaryPrice(UtilityClass.max(project.getMaxPrice(), project.getMaxResalePrice()));

        Set<String> fields = selector.getFields();

        List<Property> properties = getPropertyFromIdAndUpdateojectField(project, true);
        /*
         * Setting properites if needed.
         */
        if (fields == null || fields.contains("properties")) {
            // Setting media (3D Images), if needed.
            if (fields == null || fields.contains("media")) {
                mediaEnricher.setPropertiesMedia(properties);
            }
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
        setProjectsFieldFromProperties(result.getResults(), false);
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
        List<Project> projects = getProjectListByIds(ids);
        imageEnricher.setImagesOfProjects(projects);
        return projects;
    }

    public List<Project> getProjectListByIds(Set<Integer> ids) {
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
        PaginatedResponse<List<Project>> paginatedResponse = projectDao.getProjects(selector);
        imageEnricher.setImagesOfProjects(paginatedResponse.getResults());
        setProjectsFieldFromProperties(paginatedResponse.getResults(), false);
        return paginatedResponse;
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

    public Map<String, Integer> getProjectCountByCities(Integer builderId) {
        return projectSolrDao.getProjectCountByCities(builderId);
    }

    @Cacheable(value = Constants.CacheName.PROPERTY_INACTIVE)
    public Integer getProjectIdForPropertyId(Integer propertyId) {
        return projectDao.getProjectIdForPropertyId(propertyId);
    }

    @Cacheable(value = Constants.CacheName.PROJECT_INACTIVE)
    public Project getActiveOrInactiveProjectById(Integer projectId) {
        return projectDao.findActiveOrInactiveProjectById(projectId);
    }

    // This method will divide the Safety and Livability scores by 2 for
    // backward compatibility
    // of API's, as all these scores now will be based on 10 and earlier it was
    // based on 5.
    public void updateLifestyleScoresByHalf(List<Project> results) {
        if (results == null || results.isEmpty()) {
            return;
        }

        for (Project project : results) {
            if (project.getSafetyScore() != null) {
                project.setSafetyScore(project.getSafetyScore() / 2);
            }

            if (project.getLivabilityScore() != null) {
                project.setLivabilityScore(project.getLivabilityScore() / 2);
            }

            if (project.getProjectLocalityScore() != null) {
                project.setProjectLocalityScore(project.getProjectLocalityScore() / 2);
            }

            if (project.getProjectSocietyScore() != null) {
                project.setProjectSocietyScore(project.getProjectSocietyScore() / 2);
            }

            if (project.getLocality() != null) {
                localityService.updateLocalitiesLifestyleScoresAndRatings(Collections.singletonList(project
                        .getLocality()));
            }
        }
    }

    /**
     * This method will take list of projects and set the project fields derived
     * from properies of that project.
     * 
     * @param projects
     */
    private void setProjectsFieldFromProperties(List<Project> projects, boolean isCouponCatalogueNeeded) {
        if (projects == null || projects.isEmpty())
            return;

        for (Project project : projects) {
            getPropertyFromIdAndUpdateojectField(project, isCouponCatalogueNeeded);
        }
    }

    /**
     * This method will set fields derived from the properties of a project to
     * the project object. It will return the list of properties for a project.
     * 
     * @param project
     * @return List of properties of a project.
     */
    @Cacheable(value = Constants.CacheName.PROJECT)
    private List<Property> getPropertyFromIdAndUpdateojectField(Project project, boolean isCouponCatalogueNeeded) {
        List<Property> properties = propertyService.getPropertiesForProject(project.getProjectId());
        if (isCouponCatalogueNeeded == true) {
            propertyService.setCouponCatalogueForProperties(properties);
        }

        CouponCatalogue couponCatalogue = null;
        int totalCouponsLeft = 0, totalCoupons = 0;

        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            Double pricePerUnitArea = property.getPricePerUnitArea();
            Double primaryPrice = property.getBudget();

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

            if (property.isCouponAvailable()) {
                couponCatalogue = property.getCouponCatalogue();
                project.setMaxDiscount(UtilityClass.max(project.getMaxDiscount(), couponCatalogue.getDiscount()));
                project.setCouponAvailable(true);
                totalCouponsLeft += couponCatalogue.getInventoryLeft();
                totalCoupons += couponCatalogue.getTotalInventory();

                if (primaryPrice != null) {
                    project.setMinDiscountPrice(UtilityClass.min(
                            project.getMinDiscountPrice(),
                            primaryPrice - couponCatalogue.getDiscount()));
                    project.setMaxDiscountPrice(UtilityClass.max(
                            project.getMaxDiscountPrice(),
                            primaryPrice - couponCatalogue.getDiscount()));

                }

            }

            property.setProject(null);
        }
        
        if (isCouponCatalogueNeeded == true) {
            project.setTotalCouponsInventory(totalCoupons);
            project.setCouponsInventoryLeft(totalCouponsLeft);
        }

        return properties;
    }
}
