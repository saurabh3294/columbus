/**
 * 
 */
package com.proptiger.data.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Property;
import com.proptiger.data.repo.PropertyDao;

/**
 * @author mandeep
 *
 */
@Service
public class PropertyService {
    @Autowired
    PropertyDao propertyDao;

    public List<Property> getProperties() {
        return Collections.singletonList(propertyDao.findOne("PROPERTY-10000"));
    }
}
