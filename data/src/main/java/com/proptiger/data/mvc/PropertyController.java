/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Property;
import com.proptiger.data.service.PropertyService;
import org.apache.solr.client.solrj.SolrServerException;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping(value = "v1/entity/property")
public class PropertyController {
    
    PropertyService propertyService = new PropertyService();

    @RequestMapping
    public @ResponseBody Object getProperties() throws SolrServerException {
        return propertyService.getProperties();
    }
}
