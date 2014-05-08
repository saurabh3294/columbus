package com.proptiger.app.mvc;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.CompositeAPIService;

/**
 * This class receives multiple url as request parameter and call server for
 * each of the url to get result and then put that into a hash map. This API can
 * be used to saved to save multiple HTTP request
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "app/v1/composite")
public class AppCompositAPIController {

    @Autowired
    private CompositeAPIService compositeAPIService;

    @RequestMapping
    @ResponseBody
    public APIResponse getCompositeApiResult(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true, value = "api") List<String> api) {
        Map<String, Object> responseMap = null;
        responseMap = compositeAPIService.getResponseForApis(api);
        return new APIResponse(responseMap);
    }

}
