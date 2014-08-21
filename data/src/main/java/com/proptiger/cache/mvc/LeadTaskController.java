package com.proptiger.cache.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.external.dto.LeadTaskDto;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.LeadTaskService;

/**
 * 
 * @author azi
 * 
 */
@Controller
public class LeadTaskController {
    @Autowired
    private LeadTaskService taskService;

    @RequestMapping(value = "data/v1/entity/user/lead-offer-task/{taskId}", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateLeadTask(@RequestBody LeadTaskDto leadTask, @PathVariable int taskId) {
        leadTask.setId(taskId);
        return new APIResponse(taskService.updateTask(leadTask));
    }
}
