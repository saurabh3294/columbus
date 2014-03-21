package com.proptiger.data.mvc.b2b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.Catchment;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.b2b.CatchmentService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping
public class CatchmentController extends BaseController {
    @Autowired
    private CatchmentService catchmentService;

    @RequestMapping(value = "/data/v1/entity/user/catchment", method = RequestMethod.POST)
    public @ResponseBody
    ProAPIResponse createCatchment(
            @RequestBody Catchment catchment,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        return new ProAPISuccessResponse(catchmentService.createCatchment(catchment, userInfo));
    }

    @RequestMapping(value = "/data/v1/entity/user/catchment", method = RequestMethod.GET)
    public @ResponseBody
    ProAPIResponse getCatchment(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @ModelAttribute FIQLSelector fiqlSelector) {
        return new ProAPISuccessResponse(catchmentService.getCatchment(fiqlSelector
                .addAndConditionToFilter("userId==" + userInfo.getUserIdentifier())));
    }

    @RequestMapping(value = "/data/v1/entity/user/catchment", method = RequestMethod.PUT)
    public @ResponseBody
    ProAPIResponse updateCatchment(
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo,
            @RequestBody Catchment catchment) {
        return new ProAPISuccessResponse(catchmentService.updateCatchment(catchment, userInfo));
    }
}
