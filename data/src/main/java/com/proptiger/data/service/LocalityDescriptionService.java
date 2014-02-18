package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;
import com.proptiger.mail.service.TemplateToHtmlGenerator;

/**
 * This class is responsible to create locality description from various set of
 * templates. Template for a locality will be selected by 
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class LocalityDescriptionService {
	
	@Value("${locality.template.base.path}")
	private String localityTemplateBasePath;
	
	@Value("${locality.template.files}")
	private List<String> templateFiles;
	
	@Autowired
	private TemplateToHtmlGenerator templateToHtmlGenerator;

	@Autowired
	private LocalityService localityService;
	
	@Autowired
	private LocalityRatingService localityRatingService;
	
	@Autowired
	private LocalityAmenityService localityAmenityService;
	
	@Autowired
	private BuilderService builderService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private PropertyService propertyService;
	
	/**
	 * Get locality description
	 * @param localityId
	 * @return
	 */
	public String getLocalityDescriptionUsingTemplate(Integer localityId){
		Locality locality = localityService.getLocalityInfo(localityId, 0);
		if(locality == null){
			throw new ResourceNotAvailableException(ResourceType.LOCALITY, localityId, ResourceTypeAction.GET);
		}
		String description = createLocalityDescription(localityId, locality);
		return description;
	}

	/**
	 * Create locality description by selecting appropriate template file
	 * 
	 * @param localityId
	 * @param localityTemplateDto
	 * @return
	 */
	private String createLocalityDescription(Integer localityId, 
			Locality locality) {
		String templateFile = chooseTemplateFileForLocality(localityId);
		String description = "";
		if (templateFile != null) {
			Map<String, Object> map = createTemplateInputDataMap(locality);
			description = templateToHtmlGenerator.generateHtmlFromTemplate(map, templateFile);
		}
		return description;
	}

	/**
	 * Creating map of data that will act as input for template files
	 * @param locality
	 * @return
	 */
	private Map<String, Object> createTemplateInputDataMap(Locality locality) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("locality", locality);
		map.put("nearByLocalities",
				localityService.getTopRatedLocalitiesAroundLocality(
						locality.getLocalityId(), new Selector(), 0, 0.0));
		map.put("topBuilders", builderService.getTopBuildersForLocality(locality.getLocalityId()));
		
		map.put("amenities",
				localityAmenityService.getLocalityAmenities(
						locality.getLocalityId(), null));
		map.put("popularProjects",
				projectService.getPopularProjects(new Selector()));
		Selector selector = new Gson().fromJson(
				"{\"filters\":{\"and\":[{\"equal\":{\"localityId\":"
						+ locality.getLocalityId() + "}}]}}", Selector.class);
		List<Property> properties = propertyService.getProperties(selector);
		map.put("properties",	properties);
		return map;
	}

	/**
	 * Choose template file based on locality id module total number of template
	 * files. So for a locality id template selection is fixed. If no template files are specified in 
	 * application.properties then this method will return null.
	 * 
	 * @param localityId
	 * @return
	 */
	private String chooseTemplateFileForLocality(Integer localityId) {
		String templatePath = null;
		if(templateFiles != null && templateFiles.size() > 0){
			int fileIndex = localityId % templateFiles.size();
			//fileIndex = 2;
			templatePath = localityTemplateBasePath + templateFiles.get(fileIndex);
		}
		
		return templatePath;
	}
}
