/**
 * 
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ImageService;
import com.proptiger.data.service.LocalityReviewService;
import com.proptiger.data.service.LocalityService;

/**
 * @author mandeep
 * @author Rajeev Pandey
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
    
	/**
	 * This method find popular localities of either city id or suburb id.
	 * Popularity is defined by priority of all localities, if there is a tie
	 * then take the localities in which number of leads was maximum in last π
	 * weeks
	 * 
	 * @param selector
	 * @return
	 */
    @RequestMapping(value = "/popular")
    @DisableCaching // to be removed.
	public @ResponseBody
	ProAPIResponse getPopularLocalitiesOfCity(
			@RequestParam(required = false, value = "cityId") Integer cityId,
			@RequestParam(required = false, value = "suburbId") Integer suburbId,
			@RequestParam(required = false, value = "enquiryInWeeks", defaultValue = "8") Integer enquiryInWeeks,
			@RequestParam(required = false) String selector) {
		Selector localitySelector = new Selector();
		if (selector != null) {
			localitySelector = super
					.parseJsonToObject(selector, Selector.class);
		}
		List<Locality> popularLocalities = localityService
				.getPopularLocalities(cityId, suburbId, enquiryInWeeks);
		return new ProAPISuccessCountResponse(super.filterFields(
				popularLocalities, localitySelector.getFields()),
				popularLocalities.size());
	}

    @RequestMapping("/{localityId}/radius")
	@ResponseBody
	public ProAPIResponse getLocalityRadiusOnProject(@PathVariable int localityId){
		return new ProAPISuccessResponse(localityService.getMaxRadiusForLocalityOnProject(localityId));
	}
}
