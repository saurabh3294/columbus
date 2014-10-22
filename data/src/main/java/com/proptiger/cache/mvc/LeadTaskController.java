package com.proptiger.cache.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.data.external.dto.LeadTaskDto;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.LeadTaskService;
import com.proptiger.data.util.Constants;

/**
 * 
 * @author azi
 * 
 */
@DisableCaching
@Controller
@RequestMapping
public class LeadTaskController extends BaseController {
    @Autowired
    private LeadTaskService taskService;

    @RequestMapping(value = "data/v1/entity/user/lead-offer-task/{taskId}", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateLeadTask(@RequestBody LeadTaskDto leadTask, @PathVariable int taskId) {
        leadTask.setId(taskId);
        return new APIResponse(taskService.updateTask(leadTask));
    }

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/user/lead-offer-task")
    @ResponseBody
    public APIResponse getLeadTask(
            @ModelAttribute FIQLSelector selector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser user) {
        PaginatedResponse<List<LeadTask>> leadTasksForUser = taskService.getLeadTasksForUser(
                selector,
                Integer.parseInt(user.getUserId()));
        return new APIResponse(
                super.filterFieldsFromSelector(leadTasksForUser, selector),
                leadTasksForUser.getTotalCount());
    }

    @RequestMapping(value = "/data/v1/user/config/marketplace/tasks", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getMasterTaskDetails() {
        return new APIResponse(taskService.getMasterTaskDetails());
    }
}