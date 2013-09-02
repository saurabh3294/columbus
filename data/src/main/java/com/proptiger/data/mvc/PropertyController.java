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
import com.proptiger.data.model.filter.PropertyRequestParams;
import com.proptiger.data.service.PropertyService;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping(value = "v1/entity/property")
public class PropertyController extends BaseController {
    @Autowired
    PropertyService propertyService;

    @RequestMapping
    public @ResponseBody Object getProperties(PropertyRequestParams propertyRequestParams) {
        List<Property> properties = propertyService.getProperties(propertyRequestParams);
        String fieldsString = propertyRequestParams.getFields();
        String[] fields = null;
        if (fieldsString != null && !fieldsString.isEmpty()) {
            fields = fieldsString.split(",");
        }
        
        return super.filterFields(properties, fields);
    }
}
