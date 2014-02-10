package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.ProjectDiscussion;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.ProjectDiscussionsService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping(value = "data/v1/entity/user/projectComments")
public class ProjectDiscussionsController extends BaseController{
	
	@Autowired
	private ProjectDiscussionsService projectDiscussionsService;
	
	@ResponseBody
	@RequestMapping(method= RequestMethod.POST)
	public ProAPIResponse saveProjectComments(@RequestBody ProjectDiscussion projectDiscussion, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo){
		return new ProAPISuccessResponse( projectDiscussionsService.saveProjectComments(projectDiscussion, userInfo ) );
	}
	
	@ResponseBody
	@RequestMapping(value="/{commentId}/likes", method= RequestMethod.POST)
	public ProAPIResponse incrementProjectCommentLikes(@PathVariable long commentId, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo){
		return new ProAPISuccessResponse(projectDiscussionsService.incrementProjectCommentLikes(commentId, userInfo));
	}
	
}