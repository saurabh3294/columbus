/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.ImageEnricher;
import com.proptiger.data.service.RecommendationService;

/**
 * 
 * @author mukand
 */
@Controller
@RequestMapping(value = "data/v1/recommendation")
public class RecommendationController extends BaseController {
    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ImageEnricher         imageEnricher;

    @ResponseBody
    @RequestMapping(params = { "type=similar", "propertyId" }, method = RequestMethod.GET)
    public APIResponse getSimilarProperties(
            @RequestParam(value = "propertyId") Long propertyId,
            @RequestParam(value = "limit", required = false) Integer limit) {
        if (limit == null)
            limit = 4;

        List<Property> properties = recommendationService.getSimilarProperties(propertyId, limit);
        imageEnricher.setPropertiesImages(properties);
        return new APIResponse(super.filterFields(properties, null));
    }

    @ResponseBody
    @RequestMapping(params = { "type=similar", "projectId" }, method = RequestMethod.GET)
    public APIResponse getSimilarProjects(@RequestParam(value = "projectId") int projectId, @RequestParam(
            value = "limit",
            required = false) Integer limit) {
        if (limit == null)
            limit = 4;

        List<Project> projects = recommendationService.getSimilarProjects(projectId, limit);
        // imageEnricher.setProjectMainImage(projects);

        return new APIResponse(super.filterFields(projects, null));
    }
}
