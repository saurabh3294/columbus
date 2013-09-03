/**
 * 
 */
package com.proptiger.data.mvc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public @ResponseBody Object getProperties(@RequestParam(required=false) String search) throws Exception {
    	
    	PropertyRequestParams propRequestParam = super.parseJsonToObject(search, PropertyRequestParams.class);
    	if(propRequestParam == null){
    		propRequestParam = new PropertyRequestParams();
    	}
        List<Property> properties = propertyService.getProperties(propRequestParam);
        Set<String> fieldsSet = propRequestParam.getFields();
        
        return super.filterFields(properties, fieldsSet);
    }
    
    
    public static void main(String args[]){
    	String str = "{\"fields\":[\"price_per_unit_area\",\"unit_type\",\"bedrooms\",\"unit_name\"],\"start\":0,\"rows\":20}";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			PropertyRequestParams propertyRequestParams = mapper.readValue(str, PropertyRequestParams.class);
			System.out.println();
    	} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println();
    }
}
