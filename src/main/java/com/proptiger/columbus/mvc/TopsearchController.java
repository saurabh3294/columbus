/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.mvc;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.columbus.util.Topsearch;
import com.proptiger.columbus.service.TopsearchService;
import com.proptiger.core.annotations.Intercepted;
import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.service.ApiVersionService;

/**
 * 
 * @author Manmohan

 */
@Controller
@DisableCaching
public class TopsearchController extends BaseController {

    @Autowired
    private TopsearchService  topsearchService;

    @Autowired
    private ApiVersionService apiVersionService;

    @Intercepted.TypeaheadListing
    @RequestMapping(value = "app/v1/topsearch")
    @ResponseBody
    public APIResponse getTopsearches(
            @RequestParam String entityType,
            @RequestParam int entityId,
            @RequestParam String requiredEntities
         ) {
    	
        List<Topsearch> list = topsearchService.getTopsearches(entityId, requiredEntities);

        return new APIResponse(super.filterFields(list, null), list.size());
    }

}
