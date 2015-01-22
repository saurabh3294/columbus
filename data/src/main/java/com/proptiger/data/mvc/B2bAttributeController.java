package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.B2BAttributeService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
public class B2bAttributeController {

    @Autowired
    private B2BAttributeService b2bAttributeService;

    @RequestMapping(method = RequestMethod.GET, value = "data/v1/entity/b2b/attribute")
    @ResponseBody
    public APIResponse getAttribute(@RequestParam(value = "attributeName", required = true) String attributeName) {
        return new APIResponse(b2bAttributeService.getAttributeByName(attributeName));
    }
}
