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
import org.apache.solr.client.solrj.SolrServerException;

/**
 * @author mandeep
 *
 */
@Service
public class PropertyService {
    
    PropertyDao propertyDao = new PropertyDao();

    public Object getProperties() throws SolrServerException{
        return propertyDao.getProperties();
        //return Collections.singletonList(propertyDao.findOne("PROPERTY-10000"));
    }
}
