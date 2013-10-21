/**
 * 
 */
package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.SuburbService;

/**
 * @author mandeep
 *
 */
@RequestMapping("data/v1/entity/suburb")
@Controller
public class SuburbController extends BaseController {
    @Autowired
    private SuburbService suburbService;
    
    @RequestMapping
    public @ResponseBody ProAPIResponse getLocalities(@RequestParam(required=false) String selector) {
        Selector suburbSelector = new Selector();
        if (selector != null) {
            suburbSelector = super.parseJsonToObject(selector, Selector.class);
        }
        
        return new ProAPISuccessResponse(super.filterFields(suburbService.getSuburbs(suburbSelector), suburbSelector.getFields()));
    }
}
