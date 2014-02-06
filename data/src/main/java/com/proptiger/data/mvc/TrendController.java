package com.proptiger.data.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.TrendService;

@Controller
@RequestMapping("data/v1/trend")
public class TrendController extends BaseController{
	@Autowired
    private TrendService trendService;
	
	@RequestMapping
    public @ResponseBody
    ProAPIResponse getTrends(@ModelAttribute FIQLSelector selector, @RequestParam(value="rangeField", required = false) String rangeField, @RequestParam(value="rangeValue", required = false) String rangeValue) throws Exception {
		Object response = new Object();
		if(rangeField == null || rangeValue == null){
			response = super.groupFieldsAsPerSelector(trendService.getTrend(selector), selector);
		}
		else {
			Map<String, List<InventoryPriceTrend>> serviceResponse = trendService.getBudgetSplitTrend(selector, rangeField, rangeValue);
			Map<String, Object> finalResponse = new HashMap<>();
			for(String key: serviceResponse.keySet()){
				finalResponse.put(key, super.groupFieldsAsPerSelector(serviceResponse.get(key), selector));
			}
			response = finalResponse;
		}
        return new ProAPISuccessResponse(response);
    }
	
	@RequestMapping(value = "/current")
    @ResponseBody
	public ProAPIResponse getCurrentTrend(@ModelAttribute FIQLSelector selector, @RequestParam(value="rangeField", required = false) String rangeField, @RequestParam(value="rangeValue", required = false) String rangeValue) throws Exception {
		selector.setFilters(selector.getFilters() + ";month==" + trendService.getMostRecentDate());
		return getTrends(selector, rangeField, rangeValue);
	}
}