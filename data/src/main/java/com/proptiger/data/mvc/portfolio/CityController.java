package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.City;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.portfolio.CityService;

@Controller
@RequestMapping(value = "data/v1/entity/city")
public class CityController extends BaseController{

	@Autowired
	private CityService cityService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getCities(@RequestParam(required = false, value = "selector") String selectorStr){
		Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
		List<City> list = cityService.getCityList(selector);
		
		return new ProAPISuccessResponse(super.filterFields(list, selector.getFields()), list.size());
	}
}
