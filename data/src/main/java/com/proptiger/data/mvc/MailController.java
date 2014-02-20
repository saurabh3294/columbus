package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.ProjectService;

/**
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/email")
public class MailController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping
    @ResponseBody
    public ProAPIResponse sendProjectDetailsMail(@RequestParam(value = "to") String to, @RequestParam(
            value = "projectId") Integer projectId) {
        boolean status = projectService.sendProjectDetailsMail(to, projectId);
        return new ProAPISuccessResponse(status);
    }

}
