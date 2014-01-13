/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping
public class PropertyController extends BaseController {
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private ImageService imageService;
    
    private static Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @RequestMapping(value = "data/v1/entity/property")
    public @ResponseBody ProAPIResponse getProperties(@RequestParam(required=false, value = "selector") String selector) throws Exception {
    	
    	Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
    	if(propRequestParam == null){
    		propRequestParam = new Selector();
    	}
        List<Property> properties = propertyService.getProperties(propRequestParam);
        Set<String> fieldsSet = propRequestParam.getFields();
        
        return new ProAPISuccessResponse(super.filterFields(properties, fieldsSet));
    }
    
    @RequestMapping(value = "data/v2/entity/property")
    public @ResponseBody ProAPIResponse getV2Properties(@ModelAttribute FIQLSelector selector) throws Exception {
        PaginatedResponse<List<Property>> response = propertyService.getProperties(selector);
        return new ProAPISuccessCountResponse(response.getResults(), response.getTotalCount());
    }
}
