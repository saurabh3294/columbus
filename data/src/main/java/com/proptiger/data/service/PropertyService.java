/**
 * 
 */
package com.proptiger.data.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.filter.PropertyFilter;
import com.proptiger.data.repo.PropertyDao;

/**
 * @author mandeep
 *
 */
@Service
public class PropertyService {
    
    PropertyDao propertyDao = new PropertyDao();

    public Object getProperties(PropertyFilter propertyFilter) throws SolrServerException{
        return propertyDao.getProperties(propertyFilter);
        //return Collections.singletonList(propertyDao.findOne("PROPERTY-10000"));
    }
}
