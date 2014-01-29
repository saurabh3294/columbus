/**
 * 
 */
package com.proptiger.app.mvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.model.Property;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.BuilderService;
import com.proptiger.data.service.ImageEnricher;
import com.proptiger.data.service.LocalityAmenityService;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.LocalityService;
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
    private ImageEnricher imageEnricher;

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private BuilderService builderService;
    
    @Autowired
    private ProjectAmenityService projectAmenityService;
    
    @Autowired
    private LocalityAmenityService localityAmenityService;
    
    @Autowired
    private LocalityReviewService localityReviewService;
    
    @Autowired
    private LocalityService localityService;

	private static Logger logger = LoggerFactory.getLogger(ProjectDetailController.class);
    
    @RequestMapping(value="app/v1/project-detail")
    public @ResponseBody ProAPIResponse getProjectDetails(@RequestParam(required = false) String propertySelector, @RequestParam int projectId) throws Exception {
        
        Selector propertyDetailsSelector = super.parseJsonToObject(propertySelector, Selector.class);
        if(propertyDetailsSelector == null) {
            propertyDetailsSelector = new Selector();
        }

        List<Property> properties = propertyService.getProperties(projectId);
        ProjectSpecification projectSpecification = projectService.getProjectSpecificationsV2(projectId);
        ProjectDB projectInfo = projectService.getProjectDetails(projectId);
        Builder builderDetails = builderService.getBuilderInfo(projectInfo.getBuilderId(), null);
                        
        // getting project discussions.
        int totalProjectDiscussion=0;
        List<ProjectDiscussion> projectDiscussionList = projectService.getDiscussions(projectId, null);
        if(projectDiscussionList!=null)
        	totalProjectDiscussion = projectDiscussionList.size();
        
        // getting Project Neighborhood.
        List<LocalityAmenity> listLocalityAmenity = localityAmenityService.getLocalityAmenities(projectInfo.getLocalityId(), null);
        
        Double pricePerUnitArea;
        Double resalePrice;
        if(properties.size() > 0)
        {
        	// setting images.
        	imageEnricher.setPropertiesImages(properties);
        	Property property;
        	for(int i=0; i<properties.size(); i++){
        		property = properties.get(i);
           		pricePerUnitArea = property.getPricePerUnitArea();
           		
           		if(pricePerUnitArea == null)
           			pricePerUnitArea = 0D;
           			
           		// set Primary Prices.
           		projectInfo.setMinPricePerUnitArea( UtilityClass.min(pricePerUnitArea, projectInfo.getMinPricePerUnitArea() ) );
           		projectInfo.setMaxPricePerUnitArea( UtilityClass.max(pricePerUnitArea, projectInfo.getMaxPricePerUnitArea() ) );
           		// setting distinct bedrooms
           		projectInfo.addDistinctBedrooms(property.getBedrooms());
           		projectInfo.addPropertyUnitTypes(property.getUnitType());
           		
           		// setting resale Price
            	resalePrice = property.getResalePrice();
            	projectInfo.setMaxResalePrice(UtilityClass.max(resalePrice, projectInfo.getMaxResalePrice()));
            	projectInfo.setMinResalePrice(UtilityClass.min(resalePrice, projectInfo.getMinResalePrice()));
            	
        	}
        }
        
        // getting Locality, Suburb, City Details and getting project price ranges from properties data.
        Locality locality = localityService.getLocality(projectInfo.getLocalityId());
        /*
         *  Setting locality Ratings And Reviews
         */
        localityService.updateLocalityRatingAndReviewDetails(locality);
        
        Set<String> propertyFieldString = propertyDetailsSelector.getFields();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("specification", projectSpecification.getSpecifications());
        response.put("projectDetails", projectInfo);
        response.put("builderDetails", super.filterFields(builderDetails, null));
        response.put("properties", super.filterFields(properties, propertyFieldString));
        response.put("totalProjectDiscussions", totalProjectDiscussion);
        response.put("projectAmenity", projectAmenityService.getCMSAmenitiesByProjectId(projectId));
        response.put("neighborhood", listLocalityAmenity);
        response.put("locality", locality);
        
        return new ProAPISuccessResponse(super.filterFields(response, propertyDetailsSelector.getFields()));
    }
    
    @RequestMapping(value="app/v2/project-detail")
    @Deprecated
    public @ResponseBody ProAPIResponse getProjectDetails2(@RequestParam(required = false) String selector, @RequestParam int projectId) throws Exception {
    	Selector projectSelector = super.parseJsonToObject(selector, Selector.class);
        if(projectSelector == null) {
            projectSelector = new Selector();
        }
        
        Project project = projectService.getProjectInfoDetails(projectSelector, projectId);
    	return new ProAPISuccessResponse( super.filterFields(project, projectSelector.getFields() ) );
    }
   
    @RequestMapping(value = {"app/v2/project-detail/{projectId}"})
	@ResponseBody
	@DisableCaching
	public ProAPIResponse getProjectDetails2(
			@PathVariable Integer projectId,
			@RequestParam(required = false) String selector
			) throws Exception {
		Selector projectSelector = super.parseJsonToObject(selector,
				Selector.class);
		if (projectSelector == null) {
			projectSelector = new Selector();
		}
		Project project = projectService.getProjectInfoDetails(projectSelector,
				projectId);
		return new ProAPISuccessResponse(super.filterFields(project,
				projectSelector.getFields()));
	} 
   
}
