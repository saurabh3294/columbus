package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.model.ProjectPaymentSchedule;
import com.proptiger.data.service.ProjectPaymentScheduleService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/project/payment-schedule")
public class ProjectPaymentScheduleController {

    @Autowired
    private ProjectPaymentScheduleService paymentScheduleService;

    @RequestMapping(method = RequestMethod.GET, value = "/{projectId}")
    @ResponseBody
    public APIResponse getProjectPaymentSchedules(@PathVariable Integer projectId) {
        List<ProjectPaymentSchedule> result = paymentScheduleService.getProjectPaymentSchedule(projectId);
        return new APIResponse(result, result.size());
    }
}