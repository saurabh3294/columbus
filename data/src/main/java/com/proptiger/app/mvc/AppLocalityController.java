package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Locality;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.LocalityService;

/**
 * Locality related data for specific need. Means many form of data will be
 * combined with these APIs
 * 
 * @author Mukand
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "app/v1/locality")
public class AppLocalityController extends BaseController {

	@Autowired
	private LocalityService localityService;

	/**
	 * @param cityId
	 * @return
	 */
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getLocalityListingData(@RequestParam int cityId) {
		Object object = localityService.getLocalityListing(cityId);
		return new ProAPISuccessResponse(object);
	}

	/**
	 * @param localityId
	 * @param selectorStr
	 * @return
	 */
	@RequestMapping(value = "/{localityId}", method = RequestMethod.GET)
	@ResponseBody
	@DisableCaching
	public ProAPIResponse getLocalityDetails(@PathVariable int localityId,
			@RequestParam(required = false, value = "selector") String selectorStr,
			@RequestParam(required = false, value = "imageCount", defaultValue = "3") Integer imageCount) {
		Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        if(selector == null) {
            selector = new Selector();
        }
        Locality locality = localityService.getLocalityInfo(localityId, imageCount);
		return new ProAPISuccessResponse(super.filterFieldsWithTree(locality, selector.getFields()));
	}

}