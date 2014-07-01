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

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.CompositeAPIService;
import com.proptiger.data.service.PipelineAPIService;

/**
 * 
 * @author 
 *
 */

@Controller
@RequestMapping(value = "app/v1/pipeline")
public class AppPipelineAPIController {

    @Autowired
    private PipelineAPIService pipelineAPIService;
    
    @RequestMapping
    @ResponseBody
    @DisableCaching
    public APIResponse getCompositeApiResult(HttpServletRequest request, HttpServletResponse response, @RequestParam(
            required = true,
            value = "api") List<String> api) {
        Map<String, Object> responseMap = null;
        responseMap = pipelineAPIService.getResponseForApis(api, request);
        return new APIResponse(responseMap);
    }

}
