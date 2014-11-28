package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.LocalityDescriptionService;

/**
 * This class provides api to generate description of a locality from set of
 * template files
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/locality")
@DisableCaching
public class LocalityDescriptionController {

    @Autowired
    private LocalityDescriptionService localityDescriptionService;

    @RequestMapping(value = "{localityId}/description")
    @ResponseBody
    public APIResponse getLocalityTemplate(@PathVariable Integer localityId) {
        String description = localityDescriptionService.getLocalityDescriptionUsingTemplate(localityId);
        return new APIResponse(description);
    }
}
