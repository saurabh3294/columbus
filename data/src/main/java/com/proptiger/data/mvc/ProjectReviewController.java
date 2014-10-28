package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.ProjectReview;
import com.proptiger.data.service.ProjectReviewService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@Deprecated
public class ProjectReviewController {

    @Autowired
    private ProjectReviewService projectReviewService;

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/project/{projectId}/review")
    @ResponseBody
    @DisableCaching
    public APIResponse getProjectReviewByProjectId(@PathVariable Long projectId) {
        List<ProjectReview> list = projectReviewService.getProjectReviewByProjectId(projectId);

        return new APIResponse(list);
    }
}
