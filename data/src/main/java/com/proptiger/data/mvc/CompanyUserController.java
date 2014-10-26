package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.service.companyuser.CompanyUserService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
public class CompanyUserController extends BaseController {

    @Autowired
    private CompanyUserService companyUserService;

    @RequestMapping(value = "data/v1/entity/user/company-user")
    @ResponseBody
    public APIResponse get(@ModelAttribute FIQLSelector selector , @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser activeUser) {
        CompanyUser agent = companyUserService.getAgent(activeUser.getUserIdentifier(), selector);
        return new APIResponse(super.filterFieldsFromSelector(agent, selector));
    }
}
