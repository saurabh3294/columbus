package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUserComments;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.ForumUserCommentsService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/projectComments")
public class ForumUserCommentsController extends BaseController{
	
	@Autowired
	private ForumUserCommentsService forumUserCommentsService;
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public ProAPIResponse saveProjectComments(@RequestBody ForumUserComments forumUserComments, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo){
		return new ProAPISuccessResponse( forumUserCommentsService.saveProjectComments(forumUserComments, userInfo ) );
	}

}
