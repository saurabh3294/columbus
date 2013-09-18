/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.PropertyDao;

/**
 * @author mandeep
 * 
 */
@Service
public class PropertyService {
    @Autowired
    PropertyDao propertyDao;
    
    public List<Property> getProperties(Selector propertyFilter) {
        return propertyDao.getProperties(propertyFilter);
    }

    public List<Project> getPropertiesGroupedToProjects(Selector propertyListingSelector) {
        return propertyDao.getPropertiesGroupedToProjects(propertyListingSelector);
    }

    public Map<String, List<Map<Object, Long>>> getFacets(List<String> fields) {
        return propertyDao.getFacets(fields);
    }

    public Map<String, FieldStatsInfo> getStats(List<String> fields) {
        return propertyDao.getStats(fields);
    }
}
