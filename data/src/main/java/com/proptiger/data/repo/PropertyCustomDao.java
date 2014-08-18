package com.proptiger.data.repo;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public interface PropertyCustomDao {

    public List<Property> getProperties(int projectId);
    public List<Property> getProperties(Selector selector);
    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields, Selector propertySelector);
    public PaginatedResponse<List<Project>> getPropertiesGroupedToProjects(Selector propertyListingSelector);
    public Map<String, Map<String, Integer>> getProjectDistrubtionOnStatusOnBed(Map<String, String> params);
    public Map<String, Map<String, Integer>> getProjectDistrubtionOnStatusOnMaxBed(Map<String, String> params);
    public Map<String, Map<String, Integer>> getProjectDistributionOnPrice(Map<String, Object> params);
    public SolrResult getProperty(long propertyId);
    public List<SolrResult> getSimilarProperties(
            int distance,
            Double latitude,
            Double longitude,
            double minArea,
            double maxArea,
            double minPrice,
            double maxPrice,
            String unitType,
            List<Object> projectStatus,
            int limit,
            List<Object> excludeProjectIdBedrooms,
            Double budget,
            int projectId,
            List<Object> excludeProjects);
    public List<SolrResult> getPropertiesOnProjectId(int projectId);
    public Map<String, Map<String, Integer>> getProjectStatusCountAndProjectOnLocalityByCity(Selector selector);
    public Map<String, Map<String, Integer>> getProjectStatusCountAndProjectOnLocality(int localityId);
    public int getProjectCount(Selector selector);
    public SolrQuery createSolrQuery(Selector selector);
    public PaginatedResponse<List<Property>> getProperties(FIQLSelector selector);
    public PaginatedResponse<List<Property>> getPropertiesFromDB(FIQLSelector selector);
    
}
