/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectError;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.ErrorReportingService;
import com.proptiger.data.service.ImageEnricher;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.user.ProjectDiscussionsService;

/**
 * 
 * @author mukand
 */
@Controller
@RequestMapping
public class ProjectController extends BaseController {
    @Autowired
    private ProjectService            projectService;

    @Autowired
    private ImageEnricher             imageEnricher;

    @Autowired
    private ProjectDiscussionsService projectDiscussionsService;

    @Autowired
    private ErrorReportingService     errorReportingService;

    @RequestMapping("data/v1/entity/project")
    public @ResponseBody
    APIResponse getProjects(@RequestParam(required = false, value = "selector") String selector) throws Exception {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }

        PaginatedResponse<List<Project>> response = projectService.getProjects(propRequestParam);

        Set<String> fieldsString = propRequestParam.getFields();
        return new APIResponse(
                super.filterFields(response.getResults(), fieldsString),
                response.getTotalCount());
    }

    @RequestMapping("data/v2/entity/project")
    public @ResponseBody
    APIResponse getV2Projects(@ModelAttribute FIQLSelector selector) throws Exception {
        PaginatedResponse<List<Project>> response = projectService.getProjects(selector);
        return new APIResponse(
                super.filterFieldsFromSelector(response.getResults(), selector),
                response.getTotalCount());
    }

    /**
     * Commenting this method for now as there is requirement to return new
     * projects by upcoming status. It is not possible to create new url for now
     * as it will be difficult to change for now. Do not delete the code.
     * 
     * @param cityName
     * @param selector
     * @return
     */
    /*
     * @RequestMapping(value = "/new-projects-by-launch-date")
     * 
     * @ResponseBody public APIResponse
     * getNewProjectsByLaunchDate(@RequestParam(required = false) String
     * cityName,
     * 
     * @RequestParam(required = false) String selector) { Selector
     * propRequestParam = super.parseJsonToObject(selector, Selector.class); if
     * (propRequestParam == null) { propRequestParam = new Selector(); }
     * SolrServiceResponse<List<Project>> response =
     * projectService.getNewProjectsByLaunchDate(cityName, propRequestParam);
     * 
     * for (Project project : response.getResult()) {
     * project.setImageURL(imageService.getImages(DomainObject.project, "main",
     * project.getProjectId()).get(0).getAbsolutePath()); }
     * 
     * Set<String> fieldsString = propRequestParam.getFields(); return new
     * APIResponse(super.filterFields(response.getResult(),
     * fieldsString), response.getTotalResultCount()); }
     */

    /*
     * The Request Mapping url has to be changed to
     * new-projects-by-upcoming-project-status. Temporarily using this url. It
     * has to be removed.
     */
    @RequestMapping("data/v1/entity/project/new-projects-by-launch-date")
    @ResponseBody
    public APIResponse getUpcomingNewProjects(@RequestParam(required = false) String cityName, @RequestParam(
            required = false) String selector) {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }
        PaginatedResponse<List<Project>> response = projectService.getUpcomingNewProjects(cityName, propRequestParam);

        Set<String> fieldsString = propRequestParam.getFields();
        return new APIResponse(
                super.filterFields(response.getResults(), fieldsString),
                response.getTotalCount());
    }

    @RequestMapping("data/v1/entity/project/popular")
    @ResponseBody
    public APIResponse getPopularProjects(@RequestParam(required = false, value = "selector") String selector) {
        Selector projectSelector = super.parseJsonToObject(selector, Selector.class);
        if (projectSelector == null) {
            projectSelector = new Selector();
        }
        List<Project> popularProjects = projectService.getPopularProjects(projectSelector);
        return new APIResponse(
                super.filterFields(popularProjects, projectSelector.getFields()),
                popularProjects.size());
    }

    @RequestMapping(value = "data/v1/entity/project/recently-discussed")
    @ResponseBody
    public APIResponse getRecentlyDiscussedProjects(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "4") int lastNumberOfWeeks,
            @RequestParam(required = false, defaultValue = "2") int minProjectDiscussionCount,
            @RequestParam(required = false) String selector) {

        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }
        List<Project> projects = projectService.getMostRecentlyDiscussedProjects(
                locationType.toLowerCase(),
                locationId,
                lastNumberOfWeeks,
                minProjectDiscussionCount);
        int projectCount = projects == null ? 0 : projects.size();

        return new APIResponse(super.filterFields(projects, propRequestParam.getFields()), projectCount);
    }

    @RequestMapping(value = "data/v1/entity/project/most-discussed")
    @ResponseBody
    public APIResponse getMostDiscussedProjects(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "4") int lastNumberOfWeeks,
            @RequestParam(required = false, defaultValue = "2") int minProjectDiscussionCount,
            @RequestParam(required = false) String selector) {

        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }
        List<Project> projects = projectService.getMostDiscussedProjects(
                locationType.toLowerCase(),
                locationId,
                lastNumberOfWeeks,
                minProjectDiscussionCount);
        int projectCount = projects == null ? 0 : projects.size();

        return new APIResponse(super.filterFields(projects, propRequestParam.getFields()), projectCount);
    }


    @RequestMapping(value = "/data/v1/entity/project/highest-return")
    @ResponseBody
    public APIResponse getHighestReturnProjects(
            @RequestParam String locationType,
            @RequestParam int locationId,
            @RequestParam(required = false, defaultValue = "5") int numberOfProjects,
            @RequestParam(required = false, defaultValue = "5") double minimumPriceRise,
            @RequestParam(required = false) String selector) {

        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }

        Set<String> fields = propRequestParam.getFields();
        PaginatedResponse<List<Project>> highestReturnProjects = projectService.getHighestReturnProjects(
                locationType,
                locationId,
                numberOfProjects,
                minimumPriceRise);
        return new APIResponse(
                super.filterFields(highestReturnProjects.getResults(), fields),
                highestReturnProjects.getTotalCount());
    }

    @RequestMapping(method = RequestMethod.POST, value = "data/v1/entity/project/{projectId}/report-error")
    @ResponseBody
    @DisableCaching
    public APIResponse reportProjectError(@Valid @RequestBody ProjectError projectError, @PathVariable int projectId) {
        if (projectError.getProjectId() != null)
            throw new IllegalArgumentException("Project Id should not be present in the request body");
        if (projectError.getPropertyId() != null && projectError.getPropertyId() > 0)
            throw new IllegalArgumentException(
                    "Property Id should not be present in the request body as it is for project error.");

        projectError.setProjectId(projectId);
        return new APIResponse(errorReportingService.saveReportError(projectError));
    }

}
