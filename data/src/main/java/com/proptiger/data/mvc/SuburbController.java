/**
 * 
 */
package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
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

    /**
     * Returns suburbs given a selector
     * 
     * @param selector
     * @return
     */
    @RequestMapping
    public @ResponseBody
    APIResponse getSuburbs(@RequestParam(required = false) String selector) {
        Selector suburbSelector = new Selector();
        if (selector != null) {
            suburbSelector = super.parseJsonToObject(selector, Selector.class);
        }

        return new APIResponse(super.filterFields(
                suburbService.getSuburbs(suburbSelector),
                suburbSelector.getFields()));
    }

    /**
     * Returns a suburb along with its details
     * 
     * @param suburbId
     * @return
     */
    @RequestMapping("/{suburbId}")
    @ResponseBody
    public APIResponse getSuburb(@PathVariable int suburbId) {

        return new APIResponse(super.filterFields(suburbService.getSuburb(suburbId), null));
    }
}
