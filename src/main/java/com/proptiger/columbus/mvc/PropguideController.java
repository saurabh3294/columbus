package com.proptiger.columbus.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.columbus.service.PropguideService;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;

@Controller
@DisableCaching
public class PropguideController extends BaseController {

    @Autowired
    private PropguideService propguideService;
    
    @RequestMapping(value = "app/v1/propguide")
    @ResponseBody
    public APIResponse getDocumentsV1(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int rows){

        List<PropguideDocument> results = new ArrayList<PropguideDocument>();
        results = propguideService.getDocumentsV1(query, rows);
        return new APIResponse(super.filterFields(results, null), results.size());
    }
}
