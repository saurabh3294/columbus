package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.companyuser.CompanyUserService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
public class CompanyUserController extends BaseController {

    @Autowired
    private CompanyUserService companyUserService;

    @RequestMapping(value = "data/v1/entity/company-user/{companyUserId}")
    @ResponseBody
    public APIResponse get(@PathVariable int companyUserId, @ModelAttribute FIQLSelector selector) {
        CompanyUser agent = companyUserService.getAgent(companyUserId, selector);
        return new APIResponse(super.filterFieldsFromSelector(agent, selector));
    }
}
