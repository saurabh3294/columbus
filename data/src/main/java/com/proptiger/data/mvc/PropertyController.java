/**
 * 
 */
package com.proptiger.data.mvc;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.filter.PropertyFilter;
import com.proptiger.data.service.PropertyService;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping(value = "v1/entity/property")
public class PropertyController {
    
    PropertyService propertyService = new PropertyService();

    @RequestMapping
    public @ResponseBody Object getProperties(@RequestParam PropertyFilter propertyFilter) throws SolrServerException {
        return propertyService.getProperties(propertyFilter);
    }
}
