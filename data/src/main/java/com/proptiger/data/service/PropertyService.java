/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static Logger logger = LoggerFactory.getLogger("project.review");
    
    public List<Property> getProperties(Selector propertyFilter) {
    	if(logger.isDebugEnabled()){
			logger.debug("Get Property, Request="+propertyFilter);
		}
    	
        return propertyDao.getProperties(propertyFilter);
    }
}
