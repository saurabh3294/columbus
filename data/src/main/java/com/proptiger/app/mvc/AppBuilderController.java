package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Builder;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.BuilderService;

@Controller
@RequestMapping(value="app/v1/builder")
public class AppBuilderController extends BaseController{

	@Autowired
	private BuilderService builderService;
	
	@ResponseBody
	@DisableCaching
    @RequestMapping(method = RequestMethod.GET, value = "/{builderId}")
	public ProAPISuccessResponse getBuilder(@PathVariable Integer builderId){
		Builder builder = builderService.getBuilderInfo(builderId);
		return new ProAPISuccessResponse(super.filterFieldsWithTree(builder, null));
	}
}
