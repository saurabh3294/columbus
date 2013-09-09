package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.ProjectReview;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.PropAPIResponse;
import com.proptiger.data.service.ProjectReviewService;

@Controller
@RequestMapping(value = "v1/entity/project-review")
public class ProjectReviewController {

	@Autowired
	private ProjectReviewService projectReviewService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public PropAPIResponse getProjectReviewByProjectId(
			@RequestParam Long projectId){
		List<ProjectReview> list = projectReviewService.getProjectReviewByProjectId(projectId);
		
		return new ProAPISuccessResponse(list);
	}
}
