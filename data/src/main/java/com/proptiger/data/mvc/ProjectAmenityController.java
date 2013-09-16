package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Project_Amenity;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.ProjectAmenityService;

@Controller
@RequestMapping(value = "v1/entity/amenity")
public class ProjectAmenityController {

	@Autowired
	private ProjectAmenityService amenityService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getProjectAmenities(@RequestParam(required = true, value = "projectId") Long projectId){
		List<String> list = amenityService.getAmenitiesByProjectId(projectId);
		return new ProAPISuccessResponse(list);
	}
}
