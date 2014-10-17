package com.proptiger.data.mvc.user;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.user.ProjectDiscussionsService;
import com.proptiger.data.util.Constants;

@Controller
public class ProjectDiscussionsController extends BaseController {

    @Autowired
    private ProjectDiscussionsService projectDiscussionsService;
    
    @ResponseBody
    @RequestMapping(value = "data/v1/entity/user/projectComments", method = RequestMethod.POST)
    public APIResponse saveProjectComments(
            @RequestBody ProjectDiscussion projectDiscussion,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        return new APIResponse(projectDiscussionsService.saveProjectComments(projectDiscussion, userInfo));
    }

    @ResponseBody
    @RequestMapping(value = "data/v1/entity/user/projectComments/{commentId}/likes", method = RequestMethod.POST)
    public APIResponse incrementProjectCommentLikes(
            @PathVariable long commentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        return new APIResponse(projectDiscussionsService.incrementProjectCommentLikes(commentId, userInfo));
    }

    @RequestMapping("data/v1/entity/project/{projectId}/discussions")
    @ResponseBody
    public APIResponse getDiscussions(@RequestParam(required = false) Long commentId, @PathVariable int projectId) {
        List<ProjectDiscussion> comments = projectDiscussionsService.getDiscussions(projectId, commentId);
        return new APIResponse(super.filterFields(comments, null));
    }
    
    @ResponseBody
    @RequestMapping(value = "/data/v2/entity/project/{projectId}/discussions", method = RequestMethod.GET)
    @DisableCaching
    public APIResponse getProjectComments(
            @PathVariable int projectId,
            @RequestParam(required = false) String selector) {

        Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
        if (propRequestParam == null) {
            propRequestParam = new Selector();
        }

        Set<String> fields = propRequestParam.getFields();
        PaginatedResponse<List<ProjectDiscussion>> projectComments = projectDiscussionsService.getProjectComments(
                projectId,
                propRequestParam.getPaging());

        return new APIResponse(
                super.filterFields(projectComments.getResults(), fields),
                projectComments.getTotalCount());
    }
}
