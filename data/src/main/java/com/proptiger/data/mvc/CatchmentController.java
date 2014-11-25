package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.model.Catchment;
import com.proptiger.data.service.user.CatchmentService;

@Controller
@RequestMapping
@DisableCaching
public class CatchmentController extends BaseController {
    @Autowired
    private CatchmentService catchmentService;

    @RequestMapping(value = "/data/v1/entity/user/catchment", method = RequestMethod.POST)
    public @ResponseBody
    APIResponse createCatchment(
            @RequestBody Catchment catchment,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        return new APIResponse(catchmentService.createCatchment(catchment, userInfo));
    }

    @RequestMapping(value = "/data/v1/entity/user/catchment", method = RequestMethod.GET)
    public @ResponseBody
    APIResponse getCatchment(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @ModelAttribute FIQLSelector fiqlSelector) {
        return new APIResponse(catchmentService.getCatchment(fiqlSelector.addAndConditionToFilter("userId==" + userInfo
                .getUserIdentifier())));
    }

    @RequestMapping(value = "/data/v1/entity/user/catchment", method = RequestMethod.PUT)
    public @ResponseBody
    APIResponse updateCatchment(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            @RequestBody Catchment catchment) {
        return new APIResponse(catchmentService.updateCatchment(catchment, userInfo));
    }
}
