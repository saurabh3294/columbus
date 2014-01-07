/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageEnricher;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.pojo.SolrServiceResponse;

/**
 * 
 * @author mukand
 */
@Controller
@RequestMapping(value = "data/v1/entity/project")
public class ProjectController extends BaseController {
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ImageEnricher imageEnricher;

    @RequestMapping
    public @ResponseBody
    ProAPIResponse getProjects(@RequestParam(required = false, value = "selector") String selector) throws Exception {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }

        SolrServiceResponse<List<Project>> response = projectService.getProjects(propRequestParam);

        Set<String> fieldsString = propRequestParam.getFields();
        return new ProAPISuccessCountResponse(super.filterFields(response.getResult(), fieldsString),
                response.getTotalResultCount());
    }

    /**
     * Commenting this method for now as there is requirement to return new projects by upcoming status.
     * It is not possible to create new url for now as it will be difficult to change for now. Do not 
     * delete the code.
     * @param cityName
     * @param selector
     * @return
     */
    /*@RequestMapping(value = "/new-projects-by-launch-date")
    @ResponseBody
    public ProAPIResponse getNewProjectsByLaunchDate(@RequestParam(required = false) String cityName,
            @RequestParam(required = false) String selector) {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }
        SolrServiceResponse<List<Project>> response = projectService.getNewProjectsByLaunchDate(cityName,
                propRequestParam);

        for (Project project : response.getResult()) {
            project.setImageURL(imageService.getImages(DomainObject.project, "main", project.getProjectId()).get(0).getAbsolutePath());
        }

        Set<String> fieldsString = propRequestParam.getFields();
        return new ProAPISuccessCountResponse(super.filterFields(response.getResult(), fieldsString),
                response.getTotalResultCount());
    }*/

    @RequestMapping(value = "/{projectId}/discussions")
    @ResponseBody
    @DisableCaching
    public ProAPIResponse getDiscussions(@RequestParam(required = false) Integer commentId, @PathVariable int projectId) {
        List<ProjectDiscussion> comments = projectService.getDiscussions(projectId, commentId);
        return new ProAPISuccessResponse(super.filterFields(comments, null));
    }
    
    /*
     * The Request Mapping url has to be changed to new-projects-by-upcoming-project-status. Temporarily using
     * this url. It has to be removed.
     */
    @RequestMapping(value = "/new-projects-by-launch-date")
    @ResponseBody
    public ProAPIResponse getUpcomingNewProjects(@RequestParam(required = false) String cityName,
            @RequestParam(required = false) String selector) {
        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }
        SolrServiceResponse<List<Project>> response = projectService.getUpcomingNewProjects(cityName,
                propRequestParam);
        imageEnricher.setProjectsImages(response.getResult());
        
        Set<String> fieldsString = propRequestParam.getFields();
        return new ProAPISuccessCountResponse(super.filterFields(response.getResult(), fieldsString),
                response.getTotalResultCount());
    }
    
	@RequestMapping(value = "/popular", method = RequestMethod.GET)
	@ResponseBody
	@DisableCaching
	public ProAPIResponse getPopularProjects(
			@RequestParam(required = false, value = "selector") String selector) {
		Selector projectSelector = super.parseJsonToObject(selector,
				Selector.class);
		if (projectSelector == null) {
			projectSelector = new Selector();
		}
		List<Project> popularProjects = projectService
				.getPopularProjects(projectSelector);
		return new ProAPISuccessCountResponse(super.filterFields(
				popularProjects, projectSelector.getFields()),
				popularProjects.size());
	}
}
