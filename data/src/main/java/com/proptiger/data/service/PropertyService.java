/**
 * 
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.enums.filter.Operator;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.model.cms.CouponCatalogue;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.cms.Listing.OtherInfo;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.filter.AbstractQueryBuilder;
import com.proptiger.core.model.filter.FieldsMapLoader;
import com.proptiger.core.model.filter.JPAQueryBuilder;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.Paging;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.repo.PropertyDao;

/**
 * @author mandeep
 * 
 */
@Service
public class PropertyService {
    @Autowired
    private PropertyDao            propertyDao;

    @Autowired
    private ProjectService         projectService;

    @Autowired
    private ImageEnricher          imageEnricher;

    @Autowired
    private SolrDao                solrDao;

    @Autowired
    private EntityManagerFactory   emf;

    // do not apply autowire.
    private CouponCatalogueService couponCatalogueService;

    @Autowired
    private ApplicationContext     applicationContext;

    private static int             ROWS_THRESHOLD = 200;

    public static String           cdnImageUrl;

    @Autowired
    private PropertyReader         reader;

    @PostConstruct
    private void init() {
        cdnImageUrl = reader.getRequiredProperty("cdn.image.url");
    }

    /**
     * Returns properties given a selector
     * 
     * @param propertyFilter
     * @return
     */
    public List<Property> getProperties(Selector propertyFilter) {
        List<Property> properties = propertyDao.getProperties(propertyFilter);
        imageEnricher.setPropertiesImages(properties);

        Set<String> fields = propertyFilter.getFields();
        if (fields != null && fields.contains("couponCatalogue")) {
            setCouponCatalogueForProperties(properties);
        }

        return properties;
    }

    /**
     * Returns projects given a selector on property attributes and a few more
     * like cityLabel etc. This is needed to address listing page requirements
     * where filters could be applied on property attributes to fetch project
     * objects.
     * 
     * @param propertyListingSelector
     * @return
     */
    public PaginatedResponse<List<Project>> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
        PaginatedResponse<List<Project>> projects = null;

        if (propertyListingSelector != null && propertyListingSelector.getPaging() != null
                && propertyListingSelector.getPaging().getRows() > ROWS_THRESHOLD) {
            projects = new PaginatedResponse<>();
            projects.setResults(new ArrayList<Project>());
            int startOriginal = propertyListingSelector.getPaging().getStart();
            int rowsOriginal = propertyListingSelector.getPaging().getRows();

            int remainingRowsToBeFetched = rowsOriginal;
            int rowsFetchedLast = ROWS_THRESHOLD;
            for (int start = startOriginal; remainingRowsToBeFetched > 0 && rowsFetchedLast == ROWS_THRESHOLD; start += ROWS_THRESHOLD) {
                propertyListingSelector.getPaging().setStart(start);
                propertyListingSelector.getPaging().setRows(Math.min(ROWS_THRESHOLD, remainingRowsToBeFetched));
                PaginatedResponse<List<Project>> projectsLocal = propertyDao
                        .getPropertiesGroupedToProjects(propertyListingSelector);
                projects.getResults().addAll(projectsLocal.getResults());
                projects.setTotalCount(projectsLocal.getTotalCount());
                rowsFetchedLast = projectsLocal.getResults().size();
                remainingRowsToBeFetched -= ROWS_THRESHOLD;
            }

            propertyListingSelector.getPaging().setStart(startOriginal);
            propertyListingSelector.getPaging().setRows(rowsOriginal);
        }
        else {
            projects = propertyDao.getPropertiesGroupedToProjects(propertyListingSelector);
        }

        return projects;
    }

    /**
     * Generic method to retrieve facets from Solr on properties.
     * 
     * @param fields
     *            fields on which facets need to be evaluated
     * @param propertyListingSelector
     * @return
     */
    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getFacets(fields, propertyListingSelector);
    }

    /**
     * Generic method to retrieve stats(min, max, average etc.) from Solr on
     * properties.
     * 
     * @param fields
     *            fields on which stats need to be computed
     * @param propertyListingSelector
     * @return
     */
    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertyListingSelector) {
        return getStats(fields, propertyListingSelector, null);
    }

    /**
     * Retrieves properties given a project id
     * 
     * @param projectId
     * @return
     */
    @Cacheable(value = Constants.CacheName.PROPERTY, key = "#projectId")
    public List<Property> getPropertiesForProject(int projectId) {
        Selector selector = new Selector();
        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();

        filterCriteria.put("projectId", projectId);
        searchType.put("equal", filterCriteria);
        list.add(searchType);
        filter.put("and", list);
        selector.setFilters(filter);
        selector.setPaging(new Paging(0, Integer.MAX_VALUE));

        List<Property> properties = propertyDao.getProperties(selector);
        imageEnricher.setPropertiesImages(properties);
        return properties;
    }

    public List<Property> getPropertyIdsByProjectId(Integer projectId) {
        FIQLSelector selector = new FIQLSelector();
        selector.addField("propertyId");
        selector.addAndConditionToFilter("projectId==" + projectId);
        return getProperties(selector).getResults();
    }

    public List<Property> getPropertyIdsByLocalityId(Integer localityId) {
        FIQLSelector selector = new FIQLSelector();
        selector.addField("propertyId");
        selector.addAndConditionToFilter("localityId==" + localityId);
        return getProperties(selector).getResults();
    }

    public PaginatedResponse<List<Property>> getProperties(FIQLSelector selector) {
        PaginatedResponse<List<Property>> response = propertyDao.getProperties(selector);

        Set<String> fields = selector.getFieldSet();
        if (fields != null && fields.contains("couponCatalogue")) {
            setCouponCatalogueForProperties(response.getResults());
        }

        return response;
    }

    public Map<String, Map<String, Map<String, FieldStatsInfo>>> getAvgPricePerUnitAreaBHKWise(
            String idFieldName,
            int locationId,
            String unitType) {
        Selector selector = new Selector();
        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> filterCriteria = new HashMap<>();

        filterCriteria.put(idFieldName, locationId);

        if (unitType != null) {
            filterCriteria.put("unitType", unitType);
        }
        searchType.put(Operator.equal.name(), filterCriteria);
        list.add(searchType);
        filter.put(Operator.and.name(), list);

        selector.setFilters(filter);
        selector.setPaging(new Paging(0, 0));

        return getStatsFacetsAsMaps(selector, Arrays.asList("pricePerUnitArea"), Arrays.asList("bedrooms"));
    }

    public Map<String, Map<String, Map<String, FieldStatsInfo>>> getStatsFacetsAsMaps(
            Selector selector,
            List<String> fields,
            List<String> facet) {

        Paging pagingBackUp = selector.getPaging();
        selector.setPaging(new Paging(0, 0));

        Map<String, FieldStatsInfo> stats = getStats(fields, selector, facet);
        Map<String, Map<String, Map<String, FieldStatsInfo>>> newStats = new HashMap<>();

        String fieldName, facetName;
        for (Map.Entry<String, FieldStatsInfo> entry : stats.entrySet()) {
            fieldName = entry.getKey();
            Map<String, Map<String, FieldStatsInfo>> facetsInfo = new HashMap<>();

            newStats.put(fieldName, facetsInfo);

            if (entry.getValue() == null || entry.getValue().getFacets() == null)
                continue;

            for (Map.Entry<String, List<FieldStatsInfo>> e : entry.getValue().getFacets().entrySet()) {
                facetName = e.getKey();
                List<FieldStatsInfo> details = e.getValue();
                Map<String, FieldStatsInfo> facetsMap = new HashMap<>();
                for (int i = 0; i < details.size(); i++) {
                    FieldStatsInfo fieldStatsInfo = details.get(i);
                    if (fieldStatsInfo.getCount() > 0)
                        facetsMap.put(fieldStatsInfo.getName(), fieldStatsInfo);
                }
                facetsInfo.put(facetName, facetsMap);
            }
        }

        selector.setPaging(pagingBackUp);
        return newStats;
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertySelector, List<String> facetFields) {
        SolrQuery query = propertyDao.createSolrQuery(propertySelector);
        query.add("stats", "true");

        for (String field : fields) {
            query.add("stats.field", FieldsMapLoader.getDaoFieldName(SolrResult.class, field));
        }
        if (facetFields != null) {
            for (String field : facetFields) {
                query.add("stats.facet", FieldsMapLoader.getDaoFieldName(SolrResult.class, field));
            }
        }
        Map<String, FieldStatsInfo> response = solrDao.executeQuery(query).getFieldStatsInfo();
        Map<String, FieldStatsInfo> resultMap = new HashMap<String, FieldStatsInfo>();
        for (String field : fields) {
            resultMap.put(field, response.get(FieldsMapLoader.getDaoFieldName(SolrResult.class, field)));
        }

        return resultMap;
    }

    public Property getProperty(int propertyId) {
        String jsonSelector = "{\"paging\":{\"rows\":1},\"filters\":{\"and\":[{\"equal\":{\"propertyId\":" + propertyId
                + "}}]}}";
        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);

        List<Property> properties = getProperties(selector);
        if (properties == null || properties.isEmpty())
            throw new ResourceNotAvailableException(ResourceType.PROPERTY, ResourceTypeAction.GET);

        return properties.get(0);
    }

    /*
     * 
     * Only Solr call, no DB call specific changes should be added in this
     * method
     */
    public Property getPropertyFromSolr(int propertyId) {
        String jsonSelector = "{\"paging\":{\"rows\":1},\"filters\":{\"and\":[{\"equal\":{\"propertyId\":" + propertyId
                + "}}]}}";
        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);
        List<Property> properties = propertyDao.getProperties(selector);
        if (properties == null || properties.isEmpty())
            throw new ResourceNotAvailableException(ResourceType.PROPERTY, ResourceTypeAction.GET);

        return properties.get(0);
    }

    /**
     * Tries to find a matching property from database based on other info
     * provided from database, if found used in listing otherwise create a
     * unverified property and used that while creating listing
     * 
     * @param listing
     * @param userId
     * @return
     */
    @Transactional
    public Property createUnverifiedPropertyOrGetExisting(Listing listing, Integer userId) {
        Property property = null;
        OtherInfo otherInfo = listing.getOtherInfo();
        if (otherInfo != null && otherInfo.getSize() != null
                && otherInfo.getSize() > 0
                && otherInfo.getBedrooms() > 0
                && otherInfo.getProjectId() > 0
                && otherInfo.getUnitType() != null
                && (!otherInfo.getUnitType().equals("Plot"))) {

            FIQLSelector selector = new FIQLSelector()
                    .addAndConditionToFilter("projectId==" + otherInfo.getProjectId())
                    .addAndConditionToFilter("bedrooms==" + otherInfo.getBedrooms())
                    .addAndConditionToFilter("size==" + otherInfo.getSize())
                    .addAndConditionToFilter("project.version==" + DataVersion.Website);

            if (otherInfo.getBathrooms() > 0) {
                selector.addAndConditionToFilter("bathrooms==" + otherInfo.getBathrooms());
            }
            PaginatedResponse<List<Property>> propertyWithMatchingCriteria = getPropertiesFromDB(selector);
            if (propertyWithMatchingCriteria != null && propertyWithMatchingCriteria.getResults() != null
                    && propertyWithMatchingCriteria.getResults().size() > 0) {
                // matching property object found for the given other
                // information
                property = propertyWithMatchingCriteria.getResults().get(0);
            }
            else {
                selector = new FIQLSelector().setGroup("unitType")
                        .addAndConditionToFilter("projectId==" + otherInfo.getProjectId()).setRows(1)
                        .addSortDESC("countPropertyId");

                propertyWithMatchingCriteria = getPropertiesFromDB(selector);

                if (propertyWithMatchingCriteria.getResults().get(0).getUnitType().equals(otherInfo.getUnitType())) {
                    Property toCreate = Property.createUnverifiedProperty(
                            userId,
                            otherInfo,
                            propertyWithMatchingCriteria.getResults().get(0).getUnitType());
                    property = propertyDao.saveAndFlush(toCreate);
                }
                else {
                    throw new BadRequestException("This project does not contain " + otherInfo.getUnitType());
                }
            }
        }
        else if (otherInfo != null && otherInfo.getSize() != null
                && otherInfo.getSize() > 0
                && otherInfo.getProjectId() > 0
                && otherInfo.getUnitType() != null
                && otherInfo.getUnitType().equals("Plot")) {            
           property =  creatingOrGettingPropertyInCaseOfPlot(otherInfo,userId,property);
        }
        else {
            throw new BadRequestException("Other info is invalid");
        }
        return property;
    }

    public Property creatingOrGettingPropertyInCaseOfPlot(OtherInfo otherInfo,int userId,Property property) {
        FIQLSelector selector = new FIQLSelector().addAndConditionToFilter("projectId==" + otherInfo.getProjectId())
                .addAndConditionToFilter("unitType==Plot").addAndConditionToFilter("size==" + otherInfo.getSize())
                .addAndConditionToFilter("project.version==" + DataVersion.Website);

        PaginatedResponse<List<Property>> propertyWithMatchingCriteria = getPropertiesFromDB(selector);
        if (propertyWithMatchingCriteria != null && propertyWithMatchingCriteria.getResults() != null
                && propertyWithMatchingCriteria.getResults().size() > 0) {
            property = propertyWithMatchingCriteria.getResults().get(0);
        }
        else {
            selector = new FIQLSelector().setGroup("unitType")
                    .addAndConditionToFilter("projectId==" + otherInfo.getProjectId()).addSortDESC("countPropertyId");

            propertyWithMatchingCriteria = getPropertiesFromDB(selector);

            boolean flagPlot = false;
            for (Property singleProperty : propertyWithMatchingCriteria.getResults()) {
                if (singleProperty.getUnitType().equals("Plot")) {
                    flagPlot = true;
                }
            }
            if (flagPlot == true) {
                Property toCreate = Property.createUnverifiedProperty(userId, otherInfo, "Plot");
                property = propertyDao.saveAndFlush(toCreate);
            }
            else {
                throw new BadRequestException("This project does not contain plot");
            }
        }
        return property;
    }

    public void updateProjectsLifestyleScores(List<Property> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        List<Project> projects = new ArrayList<Project>();
        for (Property property : properties) {
            projects.add(property.getProject());
        }
        projectService.updateLifestyleScoresByHalf(projects);
    }

    /**
     * Get property objects from database using filters provided in fiql
     * selector
     * 
     * @param selector
     * @return
     */
    public PaginatedResponse<List<Property>> getPropertiesFromDB(FIQLSelector selector) {
        PaginatedResponse<List<Property>> paginatedResponse = new PaginatedResponse<List<Property>>();
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<Property> builder = new JPAQueryBuilder<>(emf.createEntityManager(), Property.class);
        builder.buildQuery(selector);
        paginatedResponse.setResults(builder.retrieveResults());
        entityManager.close();
        return paginatedResponse;
    }

    /**
     * This method will take the list of property object and return the list of
     * property Ids.
     * 
     * @param properties
     * @return
     */
    private List<Integer> getPropertyIdsFromProperties(List<Property> properties) {
        List<Integer> propertyIds = new ArrayList<Integer>();

        if (properties == null || properties.isEmpty())
            return propertyIds;

        for (Property property : properties) {
            propertyIds.add(property.getPropertyId());
        }

        return propertyIds;
    }

    /**
     * This method will take the list of properties and set the coupon catalogue
     * for those properties.
     * 
     * @param properties
     */
    public void setCouponCatalogueForProperties(List<Property> properties) {
        List<Integer> propertyIds = getPropertyIdsFromProperties(properties);

        Map<Integer, CouponCatalogue> map = getCoupCatalogueService().getCouponCatalogueMapByPropertyIds(propertyIds);

        CouponCatalogue couponCatalogue;
        for (Property property : properties) {
            couponCatalogue = map.get(property.getPropertyId());
            if (couponCatalogue == null) {
                // resetting them if they are coming from solr.
                property.setCouponCatalogue(null);
                property.setCouponAvailable(null);
                continue;
            }

            property.setCouponCatalogue(map.get(property.getPropertyId()));
            property.setCouponAvailable(true);
        }
    }

    public CouponCatalogueService getCoupCatalogueService() {
        if (couponCatalogueService == null) {
            couponCatalogueService = applicationContext.getBean(CouponCatalogueService.class);
        }

        return couponCatalogueService;
    }

    public Integer getProjectIdFromDeletedPropertyId(Integer propertyId) {
        return projectService.getProjectIdForPropertyId(propertyId);
    }
}
