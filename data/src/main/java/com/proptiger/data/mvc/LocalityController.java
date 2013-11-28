/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.image.Image;
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
    
    @RequestMapping("/{localityId}/info")
    @ResponseBody
    public ProAPIResponse getLocalityInfo(@PathVariable int localityId){
    	Long totalReviews = localityReviewService.getTotalLocalityReviews(localityId);
    	int totalImages = 0;
    	List<Image> images = imageService.getImages(DomainObject.locality, null, localityId);
    	if(images != null)
    		totalImages = images.size();
    	
    	HashMap<String, Object> response = new LinkedHashMap<>();
    	response.put("totalReviews", totalReviews);
    	response.put("totalImages", totalImages);
    	
    	return new ProAPISuccessResponse(response);
    }
}
