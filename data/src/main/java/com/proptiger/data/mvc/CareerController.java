package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.JobDetail;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.CareerService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/current-openings")
public class CareerController extends BaseController{
    @Autowired
    private CareerService careerService;
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getJobDetails(@ModelAttribute FIQLSelector selector){
        PaginatedResponse<List<JobDetail>> response = careerService.getJobDetails(selector);
        return new APIResponse(
                super.filterFieldsFromSelector(response.getResults(), selector),
                response.getTotalCount());
    }
}
