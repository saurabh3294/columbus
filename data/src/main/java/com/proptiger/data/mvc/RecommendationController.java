/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
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
    
    @ResponseBody
    @RequestMapping(params={"type=similar", "propertyId"}, method=RequestMethod.GET)
    public ProAPISuccessResponse getSimilarProperties(@RequestParam(value = "propertyId")Long propertyId, @RequestParam(value="limit", required = false)Integer limit){
        if(limit == null)
            limit = 4;
 	   	
        return new ProAPISuccessResponse(super.filterFields(recommendationService.getSimilarProperties(propertyId, limit), null));
    }
    
    @ResponseBody
    @RequestMapping(params={"type=similar", "projectId"}, method=RequestMethod.GET)
    public ProAPIResponse getSimilarProjects(@RequestParam(value = "projectId")int projectId, @RequestParam(value="limit", required = false)Integer limit){
        if(limit == null)
            limit = 4;
        
        return new ProAPISuccessResponse(super.filterFields(recommendationService.getSimilarProjects(projectId, limit), null));
    }
}
