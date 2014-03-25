package com.proptiger.data.mvc.b2b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.Graph;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.b2b.UserGraphService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping
public class UserGraphController extends BaseController {
    @Autowired
    private UserGraphService userGraphService;

    @RequestMapping(value = "/data/v1/entity/user/graph", method = RequestMethod.POST)
    public @ResponseBody
    ProAPIResponse createGraph(
            @RequestBody Graph graph,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        return new ProAPISuccessResponse(userGraphService.createGraph(graph, userInfo));
    }

    @RequestMapping(value = "/data/v1/entity/user/graph", method = RequestMethod.GET)
    public @ResponseBody
    ProAPIResponse getGraph(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @ModelAttribute FIQLSelector fiqlSelector) {
        return new ProAPISuccessResponse(userGraphService.getGraph(fiqlSelector
                .addAndConditionToFilter("userId==" + userInfo.getUserIdentifier())));
    }

    @RequestMapping(value = "/data/v1/entity/user/graph", method = RequestMethod.PUT)
    public @ResponseBody
    ProAPIResponse updateGraph(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @RequestBody Graph graph) {
        return new ProAPISuccessResponse(userGraphService.updateGraph(graph, userInfo));
    }
}