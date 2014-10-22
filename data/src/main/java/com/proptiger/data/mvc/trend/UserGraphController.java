package com.proptiger.data.mvc.trend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.data.model.trend.Graph;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.trend.UserGraphService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping
public class UserGraphController extends BaseController {
    @Autowired
    private UserGraphService userGraphService;

    @RequestMapping(value = "/data/v1/entity/user/graph", method = RequestMethod.POST)
    public @ResponseBody
    APIResponse createGraph(
            @RequestBody Graph graph,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        return new APIResponse(userGraphService.createGraph(graph, userInfo));
    }

    @RequestMapping(value = "/data/v1/entity/user/graph", method = RequestMethod.GET)
    public @ResponseBody
    APIResponse getGraph(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @ModelAttribute FIQLSelector fiqlSelector) {
        return new APIResponse(userGraphService.getGraph(fiqlSelector
                .addAndConditionToFilter("userId==" + userInfo.getUserIdentifier())));
    }

    @RequestMapping(value = "/data/v1/entity/user/graph", method = RequestMethod.PUT)
    public @ResponseBody
    APIResponse updateGraph(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @RequestBody Graph graph) {
        return new APIResponse(userGraphService.updateGraph(graph, userInfo));
    }
}