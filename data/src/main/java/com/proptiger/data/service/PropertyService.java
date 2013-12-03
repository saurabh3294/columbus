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

import com.google.gson.Gson;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectSecondaryPrice;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
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
    
    public List<Property> getProperties(Selector propertyFilter) {
        return propertyDao.getProperties(propertyFilter);
    }

    public SolrServiceResponse<List<Project>> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
        SolrServiceResponse<List<Project>>  solrServiceprojects = propertyDao.getPropertiesGroupedToProjects(propertyListingSelector);
        List<Project> projects = solrServiceprojects.getResult();
        Map<Integer, ProjectSecondaryPrice> resalePrice = projectService.getAllProjectPrices();
        Gson gson = new Gson();
        int len = projects.size();
        Project project;
        for(int i=0; i<len; i++){
        	project = projects.get(i);
        	System.out.println(project.getProjectId());
        	project.setProjectSecondaryPrice(resalePrice.get( project.getProjectId() ));
        	System.out.println( gson.toJson( resalePrice.get( project.getProjectId() ) ) );
        }
        
        return solrServiceprojects;
    }

    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getFacets(fields, propertyListingSelector);
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields, Selector propertyListingSelector) {
        return propertyDao.getStats(fields, propertyListingSelector);
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
