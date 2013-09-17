/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
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

    public List<FacetField> getFacets(List<String> fields) {
        return propertyDao.getFacets(fields);
    }

    public Object getStats(List<String> fields) {
        return propertyDao.getFacets(fields);
    }
}
