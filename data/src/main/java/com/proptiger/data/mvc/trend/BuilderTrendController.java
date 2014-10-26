package com.proptiger.data.mvc.trend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.annotations.Intercepted;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.service.trend.BuilderTrendService;

/**
 * Controller for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

@Controller
@RequestMapping
public class BuilderTrendController extends BaseController{
    @Autowired
    private BuilderTrendService builderTrendService;

    @Intercepted.Trend
    @RequestMapping("app/v1/user/builder-trend/{builderId}")
    @ResponseBody
    public APIResponse getSingleBuilderTrend(
            @PathVariable Integer builderId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(builderTrendService.getBuilderTrendForSingleBuilder(builderId, userInfo));
    }

    @Intercepted.Trend
    @RequestMapping("app/v1/user/builder-trend")
    @ResponseBody
    public APIResponse getBuilderTrendFromFIQL(
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(builderTrendService.getBuilderTrend(selector, userInfo));
    }
}