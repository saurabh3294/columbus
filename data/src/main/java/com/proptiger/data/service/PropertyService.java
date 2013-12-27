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
import com.proptiger.data.model.enums.DomainObject;
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
    PropertyDao propertyDao;
    
    @Autowired
    ProjectService projectService;
    
    @Autowired
    ImageEnricher imageEnricher;
    
    public List<Property> getProperties(Selector propertyFilter) {
        List<Property> properties = propertyDao.getProperties(propertyFilter);
        imageEnricher.setPropertiesImages(null, properties);
        
        return properties;
    }

    public SolrServiceResponse<List<Project>> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
    	SolrServiceResponse<List<Project>> projects = propertyDao.getPropertiesGroupedToProjects(propertyListingSelector);
    	imageEnricher.setProjectsImages("main", projects.getResult());
    	
    	return projects;
    }

    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getFacets(fields, propertyListingSelector);
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getStats(fields, propertyListingSelector, null);
    }

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
    	selector.setPaging(new Paging(0, 1000));
    	
    	return propertyDao.getProperties(selector);
    }
}
