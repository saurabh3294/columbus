/**
 * 
 */
package com.proptiger.app.mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Project.NestedProperties;
import com.proptiger.data.model.image.Image;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.pojo.SolrServiceResponse;

/**
 * @author mandeep
 * 
 */
@Controller
@RequestMapping(value = "app/v1/project-listing")
public class ProjectListingController extends BaseController {
    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ImageService imageService;

    @RequestMapping
    public @ResponseBody
    Object getProjectListings(@RequestParam(required = false) String selector,
            @RequestParam(required = false) String facets, @RequestParam(required = false) String stats) {
        Selector projectListingSelector = super.parseJsonToObject(selector, Selector.class);
        if (projectListingSelector == null) {
            projectListingSelector = new Selector();
        }

        SolrServiceResponse<List<Project>> projects = propertyService.getPropertiesGroupedToProjects(projectListingSelector);
        for (Project project : projects.getResult()) {
            List<Image> images = imageService.getImages(DomainObject.project, "main", project.getProjectId());
            if (images != null && !images.isEmpty()) {
                project.setImageURL(images.get(0).getAbsolutePath());
            }
        }

        Set<String> fields = projectListingSelector.getFields();
        processFields(fields);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("items", super.filterFields(projects.getResult(), fields));

        if (facets != null) {
            response.put("facets", propertyService.getFacets(Arrays.asList(facets.split(",")), projectListingSelector));
        }

        if (stats != null) {
            response.put("stats", propertyService.getStats(Arrays.asList(stats.split(",")), projectListingSelector));
        }

        return new ProAPISuccessCountResponse(response, projects.getTotalResultCount());
    }

    private void processFields(Set<String> fields) {
        if (fields != null) {
            Set<String> fieldsToBeAdded = new HashSet<String>();
            Iterator<String> iterator = fields.iterator();
            while (iterator.hasNext()) {
                try {
                    String field = iterator.next();
                    NestedProperties nestedProperty = Project.NestedProperties.valueOf(field);
                    for (String fieldName : nestedProperty.getFields()) {
                        fieldsToBeAdded.add(fieldName);
                    }

                    iterator.remove();
                } catch (Exception e) {
                }
            }

            fields.addAll(fieldsToBeAdded);
        }
    }
}
