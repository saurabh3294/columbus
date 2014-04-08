package com.proptiger.data.mvc.b2b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.b2b.BuilderTrendService;
import com.proptiger.data.util.Constants;

/**
 * Controller for custom builder trend
 * 
 * @author Azitabh Ajit
 * 
 */

@Controller
@RequestMapping
public class BuilderTrendController {
    @Autowired
    private BuilderTrendService builderTrendService;

    @RequestMapping("app/v1/user/builder-trend/{builderId}")
    @ResponseBody
    public ProAPIResponse getSingleBuilderTrend(
            @PathVariable Integer builderId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new ProAPISuccessResponse(builderTrendService.getBuilderTrendForSingleBuilder(builderId, userInfo));
    }

    @RequestMapping("app/v1/user/builder-trend")
    @ResponseBody
    public ProAPIResponse getBuilderTrendFromFIQL(
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new ProAPISuccessResponse(builderTrendService.getBuilderTrend(selector, userInfo));
    }
}