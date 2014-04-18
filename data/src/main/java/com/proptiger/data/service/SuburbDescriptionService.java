package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;
import com.proptiger.mail.service.TemplateToHtmlGenerator;

@Service
public class SuburbDescriptionService {
	@Value("${suburb.template.base.path}")
    private String                  suburbTemplateBasePath;

    @Value("${suburb.template.files}")
    private List<String>            templateFiles;

    @Autowired
    private TemplateToHtmlGenerator templateToHtmlGenerator;

    @Autowired
    private SuburbService         	suburbService;
    
    @Autowired
    private LocalityService         localityService;
    
    @Autowired
    private LandMarkService  localityAmenityService;
    
    @Autowired
    private BuilderService          builderService;

    @Autowired
    private ProjectService          projectService;

    @Autowired
    private PropertyService         propertyService;
    
    @Autowired
    private LocalityReviewService	localityReviewService;
    
    /**
     * Get suburb description
     * 
     * @param suburbId
     * @return
     */
    public String getSuburbDescriptionUsingTemplate(Integer suburbId) {
        Suburb suburb = suburbService.getSuburb(suburbId);
        if (suburb == null) {
            throw new ResourceNotAvailableException(ResourceType.SUBURB, suburbId, ResourceTypeAction.GET);
        }
        String description = createSuburbDescription(suburbId, suburb);
        return description;
    }

	private String createSuburbDescription(Integer suburbId, Suburb suburb) {
		String templateFile = chooseTemplateFileForSuburb(suburbId);
        String description = "";
        if (templateFile != null) {
            Map<String, Object> map = createTemplateInputDataMap(suburb);
            description = templateToHtmlGenerator.generateHtmlFromTemplate(map, templateFile);
        }
        return description;
	}
	
	 /**
     * Creating map of data that will act as input for template files
     * 
     * @param suburb
     * @return
     */
    private Map<String, Object> createTemplateInputDataMap(Suburb suburb) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("suburb", suburb);
        Paging paging = new Paging(0, 5);
        Selector suburbSelector = new Selector();
        suburbSelector.setPaging(paging);
        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"suburbId\":" + suburb.getId()
                + "}}]}}", Selector.class);
        map.put("localitiesInSuburb", localityService.getLocalities(selector).getResults());

        map.put("topBuilders", builderService.getTopBuilders(selector));
        map.put("amenities", localityAmenityService.getSuburbAmenities(suburb.getId()));
        map.put("popularProjects", projectService.getPopularProjects(selector));
        PaginatedResponse<List<LocalityReviewComments>> paginatedReviews = localityReviewService.getLocalityReview(
                null,
                new FIQLSelector().addAndConditionToFilter("locality.suburb.id=="+suburb.getId()));
        map.put("suburbReviewCount", paginatedReviews != null ? paginatedReviews.getTotalCount() : 0);
        List<Property> properties = propertyService.getProperties(selector);
        map.put("properties", properties);
        return map;
    }
    
    /**
     * Choose template file based on suburb id module total number of template
     * files. So for a suburb id template selection is fixed. If no template
     * files are specified in application.properties then this method will
     * return null.
     * 
     * @param suburbId
     * @return
     */
    private String chooseTemplateFileForSuburb(Integer suburbId) {
        String templatePath = null;
        if (templateFiles != null && templateFiles.size() > 0) {
            int fileIndex = suburbId % templateFiles.size();
            templatePath = suburbTemplateBasePath + templateFiles.get(fileIndex);
        }

        return templatePath;
    }
}
