/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.PropertyFilter;
import com.proptiger.data.service.PropertyService;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping(value = "v1/entity/property")
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @RequestMapping
    public @ResponseBody List<Property> getProperties(PropertyFilter propertyFilter) throws SolrServerException {
        return propertyService.getProperties(propertyFilter);
    }
}
