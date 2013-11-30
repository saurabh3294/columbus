package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityService;

@Controller
@RequestMapping(value="app/v1/locality")
public class AppLocalityController {

	@Autowired
	private LocalityService localityService;
	
	@RequestMapping
	@ResponseBody
	@DisableCaching
	public ProAPIResponse getLocalityListingData(@RequestParam int cityId){
		Object object = localityService.getLocalityListing(cityId);//getProjectStatusCountOnLocalityByCity(cityId);
		return new ProAPISuccessResponse(object);
	}
	
}
