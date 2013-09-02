/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.PropertyRequestParams;
import com.proptiger.data.repo.PropertyDao;

/**
 * @author mandeep
 * 
 */
@Service
public class PropertyService {
    @Autowired
    PropertyDao propertyDao;

    public List<Property> getProperties(PropertyRequestParams propertyFilter) {
        return propertyDao.getProperties(propertyFilter);
    }
}
