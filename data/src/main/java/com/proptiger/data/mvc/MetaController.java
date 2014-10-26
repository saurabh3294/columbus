package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.meta.ResourceModelMeta;
import com.proptiger.data.service.MetaService;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/resource/meta")
@DisableCaching
public class MetaController {
    @Autowired
    private MetaService metaService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getAllResourceMeta(@RequestParam(value = "resourceName", required = false) String resourceName) {

        List<ResourceModelMeta> resourceMetaList = metaService.getResourceMeta(resourceName);
        return new APIResponse(resourceMetaList);
    }
}
