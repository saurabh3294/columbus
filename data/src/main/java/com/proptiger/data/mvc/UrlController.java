package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.URLService;
import com.proptiger.data.service.URLService.ValidURLResponse;

public class UrlController extends BaseController {
    @Autowired
    private URLService urlService;
    
    @RequestMapping("data/v1/entity/project")
    @ResponseBody
    public APIResponse getProjects(@RequestParam String url) {
        ValidURLResponse validURLResponse = urlService.getURLStatus(url);
        return new APIResponse();
    }

}
