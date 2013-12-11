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

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.LocalityService;

/**
 * @author mandeep
 *
 */
@RequestMapping("data/v1/entity/locality")
@Controller
public class LocalityController extends BaseController {
    @Autowired
    private LocalityService localityService;
    @Autowired
    private LocalityReviewService localityReviewService;
    @Autowired 
    private ImageService imageService;
    
    @RequestMapping
    public @ResponseBody ProAPIResponse getLocalities(@RequestParam(required=false) String selector) {
        Selector localitySelector = new Selector();
        if (selector != null) {
            localitySelector = super.parseJsonToObject(selector, Selector.class);
        }
        
        return new ProAPISuccessResponse(super.filterFields(localityService.getLocalities(localitySelector), localitySelector.getFields()));
    }
    
    @RequestMapping("/{localityId}/radius")
	@ResponseBody
	public ProAPIResponse getLocalityRadiusOnProject(@PathVariable int localityId){
		return new ProAPISuccessResponse(localityService.getMaxRadiusForLocalityOnProject(localityId));
	}
}
