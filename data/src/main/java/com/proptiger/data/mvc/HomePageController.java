package com.proptiger.data.mvc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.HomePageService;

@Controller
@RequestMapping(value = "app/v1/homepage")
public class HomePageController {
    @Autowired
    private HomePageService homePageService;

    private static Long     CRORE_FACTOR = (long) Math.pow(10, 7);
    private static Long     MULT_FACTOR  = 100L;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getHomePageResult() {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Long noOfProperties = homePageService.getNoOfProperties();
        responseMap.put("noOfCustomers", getRoundedValue(new Long(8990)));
        responseMap.put(
                "worthOfProperties",
                ((Long) getRoundedValue(new Long(80699950243L) / CRORE_FACTOR)) * CRORE_FACTOR);
        responseMap.put("noOfAgents", getRoundedValue(new Long(369)));
        responseMap.put("noOfProperties", getRoundedValue(noOfProperties));
        return new APIResponse(responseMap);
    }

    private Object getRoundedValue(Long num) {
        return (num / MULT_FACTOR) * MULT_FACTOR;
    }
}
