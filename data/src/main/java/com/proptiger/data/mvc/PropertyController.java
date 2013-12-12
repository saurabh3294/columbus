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
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.PropertyService;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/property")
public class PropertyController extends BaseController {
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private ImageService imageService;

    @RequestMapping
    public @ResponseBody ProAPIResponse getProperties(@RequestParam(required=false, value = "selector") String selector) throws Exception {
    	
    	Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
    	if(propRequestParam == null){
    		propRequestParam = new Selector();
    	}
        List<Property> properties = propertyService.getProperties(propRequestParam);
        for (Property property : properties) {
            List<Image> images = imageService.getImages(DomainObject.project, "main", property.getProjectId());
            if (images != null && !images.isEmpty()) {
                property.getProject().setImageURL(images.get(0).getAbsolutePath());
            }

            images = imageService.getImages(DomainObject.builder, "logo", property.getProject().getBuilderId());
            if (images != null && !images.isEmpty()) {
                property.getProject().getBuilder().setImageURL(images.get(0).getAbsolutePath());
            }
        }

        Set<String> fieldsSet = propRequestParam.getFields();
        
        return new ProAPISuccessResponse(super.filterFields(properties, fieldsSet));
    }
    
    public static void main(String args[]){
    	String str = "{\"fields\":[\"price_per_unit_area\",\"unit_type\",\"bedrooms\",\"unit_name\"],\"start\":0,\"rows\":20}";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			Selector propertyRequestParams = mapper.readValue(str, Selector.class);
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
