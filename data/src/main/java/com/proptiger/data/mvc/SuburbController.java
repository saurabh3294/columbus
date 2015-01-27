/**
 * 
 */
package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.SuburbService;

/**
 * @author mandeep
 * 
 */
@RequestMapping("")
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
    @RequestMapping(value = "data/v1/entity/suburb")
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
    @RequestMapping("data/v1/entity/suburb/{suburbId}")
    @ResponseBody
    public APIResponse getSuburb(@PathVariable int suburbId, @RequestParam(required = false) String selector) {
    	Selector suburbSelector = new Selector();
        if (selector != null) {
            suburbSelector = super.parseJsonToObject(selector, Selector.class);
        }
        
        return new APIResponse(super.filterFields(suburbService.getSuburb(suburbId, false, suburbSelector), suburbSelector.getFields()));
    }
    
    /**
     * Returns a suburb along with its details
     * 
     * @param suburbId
     * @return
     */
    @RequestMapping("data/v2/entity/suburb/{suburbId}")
    @ResponseBody
    public APIResponse getV2Suburb(@PathVariable int suburbId, @RequestParam(value="selector") String selectorStr) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        if(selector == null ){
            selector = new Selector();
        }
        return new APIResponse(super.filterFields(suburbService.getSuburb(suburbId, true, selector), selector.getFields()));
    }
    
    @RequestMapping(value = "data/v1/entity/suburb/{id}/active-inactive")
    @ResponseBody
    public APIResponse getActiveInactiveSuburb(@PathVariable int id){
        return new APIResponse(suburbService.getActiveOrInactiveSuburbById(id));
    }
    
    @RequestMapping(value = "data/v1/entity/suburb/{suburbId}", method = RequestMethod.PUT)
    @ResponseBody
    public APIResponse updateSuburbDescriptiom(@RequestBody Suburb suburb, @RequestParam(
            required = false,
            value = "needUpdatedSuburb",
            defaultValue = "false") boolean needUpdatedSuburb) {
        Suburb updated = suburbService.updateSuburb(suburb);
        if (needUpdatedSuburb) {
            return new APIResponse(updated);
        }
        else {
            return new APIResponse();
        }
    }
    
}
