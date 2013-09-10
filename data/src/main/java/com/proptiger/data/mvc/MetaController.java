package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.meta.ResourceModelMeta;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.MetaService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "v1/resource/meta/**")
public class MetaController {
	
	@Autowired
	private MetaService metaService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public  ProAPIResponse getAllResourceMeta(){
		
		List<ResourceModelMeta> list = metaService.getAllResourceMeta();
		return new ProAPISuccessResponse(list);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{resourceName}")
	@ResponseBody
	public ProAPIResponse getAllResourceMeta(
			@PathVariable String resourceName) {

		ResourceModelMeta resourceMeta = metaService
				.getResourceMeta(resourceName);
		return new ProAPISuccessResponse(resourceMeta);
	}
}
