package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.ProjectAmenityService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/amenity")
public class ProjectAmenityController {

    @Autowired
    private ProjectAmenityService amenityService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getProjectAmenities(@RequestParam(required = true, value = "projectId") Long projectId) {
        List<String> list = amenityService.getAmenitiesNameByProjectId(projectId);
        return new APIResponse(list);
    }
}
