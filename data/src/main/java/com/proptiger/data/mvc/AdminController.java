package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.AdminService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
public class AdminController extends BaseController{

    @Autowired
    private AdminService adminService;
    
    @RequestMapping(value = "data/v1/entity/permission", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getUserPermissions(@RequestParam int userId){
        return new APIResponse(adminService.getUserPermissions(userId));
    }
}
