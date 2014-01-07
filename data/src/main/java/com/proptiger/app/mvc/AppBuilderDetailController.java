package com.proptiger.app.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.Builder;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.BuilderService;

/**
 * Builder details API
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value="app/v1/builder-detail")
public class AppBuilderDetailController extends BaseController{

	@Autowired
	private BuilderService builderService;
	
	/**
	 * This methods get builder details by combining data from builder model and
	 * some other derived data of builder. Selector can be used to filter out
	 * unnecessary fields
	 * 
	 * @param builderId
	 * @param selectorStr
	 * @return
	 */
	@ResponseBody
	@DisableCaching
    @RequestMapping(method = RequestMethod.GET, value = "/{builderId}")
	public ProAPISuccessResponse getBuilder(@PathVariable Integer builderId,
			@RequestParam(required = false, value = "selector") String selectorStr){
		Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        if(selector == null) {
            selector = new Selector();
        }
		Builder builder = builderService.getBuilderInfo(builderId, selector);
		return new ProAPISuccessResponse(super.filterFieldsWithTree(builder, selector.getFields()));
	}
}
