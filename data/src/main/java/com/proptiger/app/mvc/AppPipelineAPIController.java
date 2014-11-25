package com.proptiger.app.mvc;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.pojo.response.APIResponse;
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
    public APIResponse getPipelineApiResult(
            @RequestParam(required = true, value = "api") List<String> api,
            @RequestParam(required = false, value = "include") String include) {
        Map<String, Object> responseMap = null;
        responseMap = pipelineAPIService.getResponseForApis(api, include);
        return new APIResponse(responseMap);
    }

}
