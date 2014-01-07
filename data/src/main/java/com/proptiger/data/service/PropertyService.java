/**
 * 
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;

/**
 * @author mandeep
 * 
 */
@Service
public class PropertyService {
    @Autowired
    private PropertyDao propertyDao;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ImageEnricher imageEnricher;

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
     * Returns projects given a selector on property attributes and a few more like cityLabel etc.
     * This is needed to address listing page requirements where filters could be applied on
     * property attributes to fetch project objects.
     *
     * @param propertyListingSelector
     * @return
     */
    public SolrServiceResponse<List<Project>> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
    	SolrServiceResponse<List<Project>> projects = propertyDao.getPropertiesGroupedToProjects(propertyListingSelector);
    	imageEnricher.setProjectsImages(projects.getResult());
    	
    	return projects;
    }

    /**
     * Generic method to retrieve facets from Solr on properties.
     *
     * @param fields    fields on which facets need to be evaluated
     * @param propertyListingSelector
     * @return
     */
    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getFacets(fields, propertyListingSelector);
    }

    /**
     * Generic method to retrieve stats(min, max, average etc.) from Solr on properties.
     *
     * @param fields    fields on which stats need to be computed
     * @param propertyListingSelector
     * @return
     */
    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getStats(fields, propertyListingSelector, null);
    }

    /**
     * Retrieves properties given a project id
     *
     * @param projectId
     * @return
     */
    public List<Property> getProperties(int projectId) {
    	Selector selector = new Selector();
    	Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String,Map<String,Object>>>>();
    	List<Map<String, Map<String, Object>>> list = new ArrayList<>();
    	Map<String, Map<String, Object>> searchType = new HashMap<>();
    	Map<String, Object> filterCriteria = new HashMap<>();
    	
    	filterCriteria.put("projectId", projectId);
    	searchType.put("equal", filterCriteria);
    	list.add(searchType);
    	filter.put("and", list);
    	selector.setFilters(filter);
    	selector.setPaging(new Paging(0, Integer.MAX_VALUE));
    	
    	return propertyDao.getProperties(selector);
    }
}
