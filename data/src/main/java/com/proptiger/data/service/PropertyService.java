/**
 * 
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.filter.Operator;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.Listing.OtherInfo;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.FieldsMapLoader;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.repo.SolrDao;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * @author mandeep
 * 
 */
@Service
public class PropertyService {
    @Autowired
    private PropertyDao          propertyDao;

    @Autowired
    private ProjectService       projectService;

    @Autowired
    private ImageEnricher        imageEnricher;

    @Autowired
    private SolrDao              solrDao;

    @Autowired
    private EntityManagerFactory emf;

    private static int           ROWS_THRESHOLD = 200;

    /**
     * Returns properties given a selector
     * 
     * @param propertyFilter
     * @return
     */
    public List<Property> getProperties(Selector propertyFilter) {
        List<Property> properties = propertyDao.getProperties(propertyFilter);
        imageEnricher.setPropertiesImages(properties);

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

    public PaginatedResponse<List<Property>> getProperties(FIQLSelector selector) {
        return propertyDao.getProperties(selector);
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
        if (otherInfo != null && otherInfo.getSize() > 0 && otherInfo.getBedrooms() > 0 && otherInfo.getProjectId() > 0) {
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
                Property toCreate = Property.createUnverifiedProperty(userId, otherInfo, propertyWithMatchingCriteria
                        .getResults().get(0).getUnitType());
                property = propertyDao.saveAndFlush(toCreate);
            }
        }
        else {
            throw new BadRequestException("Other info is invalid");
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

}
