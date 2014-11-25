package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.SuburbDescriptionService;

/**
 * This class provides api to generate description of a suburb from set of
 * template files
 * 
 * @author Ahsan Khan
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/suburb")
@DisableCaching
public class SuburbDescriptionController {
	@Autowired
    private SuburbDescriptionService suburbDescriptionService;

    @RequestMapping(value = "{suburbId}/description")
    @ResponseBody
    public APIResponse getSuburbTemplate(@PathVariable Integer suburbId) {
        String description = suburbDescriptionService.getSuburbDescriptionUsingTemplate(suburbId);
        return new APIResponse(description);
    }
}
