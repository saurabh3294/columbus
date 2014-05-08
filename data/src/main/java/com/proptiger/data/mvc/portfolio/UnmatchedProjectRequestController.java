package com.proptiger.data.mvc.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UnmatchedProjectDetails;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.portfolio.UnmatchedProjectRequestService;
import com.proptiger.data.util.Constants;

/**
 * This class handles the request for project that is not in proptiger database,
 * that a user might try to add in his portfolio
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/unmatched-project")
public class UnmatchedProjectRequestController extends BaseController {

    @Autowired
    private UnmatchedProjectRequestService unmatchedProjectRequestService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public APIResponse submitUnmatchedProjectDetails(
            @RequestBody() UnmatchedProjectDetails unmatchedProjectDetails,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        boolean status = unmatchedProjectRequestService
                .handleUnmatchedProjectRequest(unmatchedProjectDetails, userInfo);
        return new APIResponse(status, 1);
    }

}
