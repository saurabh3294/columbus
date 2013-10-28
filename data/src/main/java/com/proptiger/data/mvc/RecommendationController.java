/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value = "data/v1/recommendation")
public class RecommendationController extends BaseController {
    @Autowired
    private RecommendationService recommendationService;
    
    @ResponseBody
    @RequestMapping(value="/similar-properties")
    public ProAPIResponse getSimilarProperties(@RequestParam(value = "propertyId")Long propertyId, @RequestParam(value="limit", required = false)Integer limit){
        if(limit == null)
            limit = 4;
        
        return new ProAPISuccessResponse(super.filterFields(recommendationService.getSimilarProperties(propertyId, limit), null));
    }
    
    @ResponseBody
    @RequestMapping(value="/similar-projects")
    public ProAPIResponse getSimilarProjects(@RequestParam(value = "projectId")int projectId, @RequestParam(value="limit", required = false)Integer limit){
        if(limit == null)
            limit = 4;
        
        return new ProAPISuccessResponse(super.filterFields(recommendationService.getSimilarProjects(projectId, limit), null));
    }
}
