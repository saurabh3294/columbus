/**
 * 
 */
package com.proptiger.app.mvc;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.model.Property;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.BuilderService;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityAmenityService;
import com.proptiger.data.service.ProjectAmenityService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.util.UtilityClass;

/**
 * @author mandeep
 *
 */
@Controller
public class ProjectDetailController extends BaseController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private BuilderService builderService;
    
    @Autowired
    private ProjectAmenityService projectAmenityService;
    
    @Autowired
    private LocalityAmenityService localityAmenityService;
    
    @RequestMapping(value="app/v1/project-detail")
    @DisableCaching // to be removed.
    public @ResponseBody ProAPIResponse getProjectDetails(@RequestParam(required = false) String propertySelector, @RequestParam int projectId) throws Exception {
        
        Selector propertyDetailsSelector = super.parseJsonToObject(propertySelector, Selector.class);
        if(propertyDetailsSelector == null) {
            propertyDetailsSelector = new Selector();
        }

        List<Property> properties = propertyService.getProperties(projectId);
        ProjectSpecification projectSpecification = projectService.getProjectSpecifications(projectId);
        Builder builderDetails = builderService.getBuilderDetailsByProjectId(projectId);
        ProjectDB projectInfo = projectService.getProjectDetails(projectId);
        Map<String, Object> parseSpecification = parseSpecificationObject(projectSpecification);
        projectInfo.setImages(imageService.getImages(DomainObject.project, null, projectId));
        
        double resalePrice;
        for (Property property : properties) {
        	// setting resale Price
        	resalePrice = property.getResalePrice();
        	projectInfo.setMaxResalePrice(UtilityClass.max(resalePrice, projectInfo.getMaxResalePrice()));
        	projectInfo.setMinResalePrice(UtilityClass.min(resalePrice, projectInfo.getMinResalePrice()));
        	
            property.setImages(imageService.getImages(DomainObject.property, null, property.getPropertyId()));
        }
        // getting project discussions.
        int totalProjectDiscussion=0;
        List<ProjectDiscussion> projectDiscussionList = projectService.getDiscussions(projectId, null);
        if(projectDiscussionList!=null)
        	totalProjectDiscussion = projectDiscussionList.size();
        // getting project Amenities
        List<String> listProjectAmenities = projectAmenityService.getAmenitiesByProjectId(projectId);
        
        // getting Project Neighborhood.
        List<LocalityAmenity> listLocalityAmenity = localityAmenityService.getLocalityAmenities(projectInfo.getLocalityId(), null);
        // getting Locality, Suburb, City Details and getting project price ranges from properties data.
        Locality locality = null;
        Double pricePerUnitArea;
        if(properties.size() > 0)
        {
        	locality = properties.get(0).getProject().getLocality();
        	
        	projectInfo.setMinPricePerUnitArea(Double.MAX_VALUE);
        	for(int i=0; i<properties.size(); i++){
           		pricePerUnitArea = properties.get(i).getPricePerUnitArea();
           		if(pricePerUnitArea == null)
           			pricePerUnitArea = 0D;
           			
           		projectInfo.setMinPricePerUnitArea( Math.min(pricePerUnitArea, projectInfo.getMinPricePerUnitArea() ) );
           		projectInfo.setMaxPricePerUnitArea( Math.max(pricePerUnitArea, projectInfo.getMaxPricePerUnitArea() ) );
        	}
        }
        
        
                
        Set<String> propertyFieldString = propertyDetailsSelector.getFields();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("specification", parseSpecification);
        response.put("projectDetails", projectInfo);
        response.put("builderDetails", super.filterFields(builderDetails, null));
        response.put("properties", super.filterFields(properties, propertyFieldString));
        response.put("totalProjectDiscussions", totalProjectDiscussion);
        response.put("projectAmenity", listProjectAmenities);
        response.put("neighborhood", listLocalityAmenity);
        response.put("locality", locality);
        
        return new ProAPISuccessResponse(super.filterFields(response, propertyDetailsSelector.getFields()));
    }
    
    private Map<String, Object> parseSpecificationObject(ProjectSpecification projectSpecification){
        if (projectSpecification == null) {
            return null;
        }

        String specsGroups[] = {"doors", "electricalFittings", "fittingsAndFixtures", "flooring", "id", "others", "walls",  "windows"};
        
        Field fields[] = projectSpecification.getClass().getDeclaredFields();
        
        Map<String, Integer> fieldsMap = new TreeMap<>();
        
        for(int i=0; i<fields.length; i++)
            fieldsMap.put(fields[i].getName(), i);
        
        Map<String, Object> parseMap = new LinkedHashMap<String, Object>();
        Map<String, Object> splitKeys;
        
        int i=0;
        String key;
        String keySuffix;
        boolean found = false;
        int index;
        Object value = null;
        for(Map.Entry<String, Integer> entry: fieldsMap.entrySet())
        {
            key = entry.getKey();
            found = false;
            while(!found && i<specsGroups.length)
            {
                found = key.startsWith(specsGroups[i++]);
            }
            if(!found)
                break;
            
            i--;
            keySuffix = key.substring(specsGroups[i].length());
            if( !parseMap.containsKey(specsGroups[i]) )
                splitKeys = new LinkedHashMap<String, Object>();
            else
                splitKeys = (Map<String, Object>)parseMap.get(specsGroups[i]);
            
            index = entry.getValue();
            try{
                fields[index].setAccessible(true);
                value = fields[index].get(projectSpecification);
            }catch(Exception e){
                value = null;
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            if(keySuffix.length() <= 0)
                parseMap.put(specsGroups[i], value);
            else
            {
                splitKeys.put(keySuffix, value);
                parseMap.put(specsGroups[i], splitKeys);
            }
        }
        
        return parseMap;
    }
    
}
