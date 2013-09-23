/**
 * 
 */
package com.proptiger.app.mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Project;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
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

    @RequestMapping
    public @ResponseBody
    Object getProjectListings(@RequestParam(required = false) String selector,
                                      @RequestParam(required = false) String facets,
                                      @RequestParam(required = false) String stats)
    {
        Selector projectListingSelector = super.parseJsonToObject(selector, Selector.class);
        if (projectListingSelector == null) {
            projectListingSelector = new Selector();
        }

        SolrServiceResponse<List<Project>> projects = propertyService.getPropertiesGroupedToProjects(projectListingSelector);
        Set<String> fields = projectListingSelector.getFields();
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("items", projects.getResult());

        if (facets != null) {
            response.put("facets", propertyService.getFacets(Arrays.asList(facets.split(",")), projectListingSelector));
        }

        if (stats != null) {
            response.put("stats", propertyService.getStats(Arrays.asList(stats.split(",")), projectListingSelector));
        }

        return super.filterFields(new ProAPISuccessResponse(response, projects.getTotalResultCount()), fields);
    }
}
