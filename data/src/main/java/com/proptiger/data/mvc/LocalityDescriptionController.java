package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityDescriptionService;

@Controller
@RequestMapping(value = "data/v1/entity/locality")
@DisableCaching
public class LocalityDescriptionController {

	@Autowired
	private LocalityDescriptionService localityDescriptionService;
	
	@RequestMapping(value = "{localityId}/description")
	@ResponseBody
	public ProAPIResponse getLocalityTemplate(@PathVariable Integer localityId){
		String description = localityDescriptionService.getLocalityDescriptionUsingTemplate(localityId);
		return new ProAPISuccessResponse(description);
	}
}
