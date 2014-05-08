package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Testimonial;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.TestimonialService;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/testimonial")
public class TestimonialController extends BaseController{
    @Autowired
    private TestimonialService testimonialService;
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getTestimonials(@ModelAttribute FIQLSelector selector){
        PaginatedResponse<List<Testimonial>> response = testimonialService.getTestimonials(selector);
        
        return new APIResponse(
                super.filterFieldsFromSelector(response.getResults(), selector),
                response.getTotalCount());
        
    }
}
