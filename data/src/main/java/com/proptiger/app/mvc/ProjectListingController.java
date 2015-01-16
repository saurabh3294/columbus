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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.proptiger.core.annotations.Intercepted;
import com.proptiger.core.enums.filter.Operator;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Project.NestedProperties;
import com.proptiger.core.model.external.GooglePlace;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.service.GooglePlacesAPIService;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;

/**
 * @author mandeep
 * 
 */
@Controller
public class ProjectListingController extends BaseController {
    @Autowired
    private PropertyService        propertyService;

    @Autowired
    private ImageService           imageService;

    @Autowired
    private ProjectService         projectService;

    @Autowired
    private GooglePlacesAPIService googlePlacesAPIService;

    private Gson                   gson                           = new Gson();

    private static double          GooglePlacesDefaultGeoDistance = 3.0d;

    @Intercepted.ProjectListing
    @RequestMapping(value = "app/v1/project-listing")
    @Cacheable(value = Constants.CacheName.CACHE)
    public @ResponseBody Object getProjectListings(@RequestParam(required = false) String selector, @RequestParam(
            required = false) String facets, @RequestParam(required = false) String stats) {

        Selector projectListingSelector = super.parseJsonToObject(selector, Selector.class);
        if (projectListingSelector == null) {
            projectListingSelector = new Selector();
        }

        PaginatedResponse<List<Project>> projects = propertyService
                .getPropertiesGroupedToProjects(projectListingSelector);

        projectService.updateLifestyleScoresByHalf(projects.getResults());
        Set<String> fields = projectListingSelector.getFields();
        processFields(fields);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("items", super.filterFields(projects.getResults(), fields));

        if (facets != null) {
            response.put("facets", propertyService.getFacets(Arrays.asList(facets.split(",")), projectListingSelector));
        }

        if (stats != null) {
            response.put("stats", propertyService.getStats(Arrays.asList(stats.split(",")), projectListingSelector));
        }
        return new APIResponse(response, projects.getTotalCount());
    }

    @Intercepted.ProjectListing
    @RequestMapping(value = "app/v2/project-listing")
    @Cacheable(value = Constants.CacheName.CACHE)
    public @ResponseBody Object getProjectListingsV2(@RequestParam(required = false) String selector, @RequestParam(
            required = false) String facets, @RequestParam(required = false) String stats, @RequestParam(
            required = false) String gpid) {
        Selector projectListingSelector = super.parseJsonToObject(selector, Selector.class);
        if (projectListingSelector == null) {
            projectListingSelector = new Selector();
        }

        /*
         * If google-place-id (gpid) is given, add a geo filter after fetching
         * place information.
         */
        if (gpid != null && !gpid.isEmpty()) {
            GooglePlace gp = googlePlacesAPIService.getPlaceDetails(gpid);
            if (gp == null) {
                throw new ProAPIException("Could not retrieve place information for google-place-id : " + gpid);
            }
            addGeoFilterToSelector(
                    projectListingSelector,
                    gp.getLatitude(),
                    gp.getLongitude(),
                    ProjectListingController.GooglePlacesDefaultGeoDistance);
        }

        PaginatedResponse<List<Project>> projects = propertyService
                .getPropertiesGroupedToProjects(projectListingSelector);

        Set<String> fields = projectListingSelector.getFields();
        processFields(fields);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("items", super.filterFields(projects.getResults(), fields));

        if (facets != null) {
            response.put("facets", propertyService.getFacets(Arrays.asList(facets.split(",")), projectListingSelector));
        }

        if (stats != null) {
            response.put("stats", propertyService.getStats(Arrays.asList(stats.split(",")), projectListingSelector));
        }

        return new APIResponse(response, projects.getTotalCount());
    }

    private void addGeoFilterToSelector(Selector selector, double latitude, double longitude, double distance) {

        /* making a temp selector with just geo-filter */
        String GeoFilterTemplate = "{\"filters\":{\"and\":[{\"geoDistance\":{\"geo\":{\"distance\":%s,\"lat\":%s,\"lon\":%s}}}]}}";
        String geoFilter = String.format(
                GeoFilterTemplate,
                String.valueOf(distance),
                String.valueOf(latitude),
                String.valueOf(longitude));
        Selector newSelector = gson.fromJson(geoFilter, Selector.class);

        /* extracting geo-filter and adding it to old selector */
        List<Map<String, Map<String, Object>>> filterList = selector.getFilters().get(Operator.and.name());
        filterList.addAll(newSelector.getFilters().get(Operator.and.name()));
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
                }
                catch (Exception e) {
                }
            }

            fields.addAll(fieldsToBeAdded);
        }
    }
}
