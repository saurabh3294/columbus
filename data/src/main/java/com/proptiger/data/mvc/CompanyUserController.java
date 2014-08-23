package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.seller.CompanyUser;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.CompanyUserService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
public class CompanyUserController extends BaseController {

    @Autowired
    private CompanyUserService companyUserService;

    @RequestMapping(value = "data/v1/entity/broker-agent/{agentId}")
    @ResponseBody
    public APIResponse get(@PathVariable int agentId, @ModelAttribute FIQLSelector selector) {
        CompanyUser agent = companyUserService.getAgent(agentId, selector);
        return new APIResponse(super.filterFieldsFromSelector(agent, selector));
    }
}
