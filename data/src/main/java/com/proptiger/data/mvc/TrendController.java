package com.proptiger.data.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping
public class TrendController extends BaseController{
	@Value("${b2b.price-inventory.max.month}")
	private String currentMonth;
	
	@Autowired
    private TrendService trendService;
	
	@RequestMapping("data/v1/trend")
    public @ResponseBody
    ProAPIResponse getTrends(@ModelAttribute FIQLSelector selector, @RequestParam(required = false) String rangeField, @RequestParam(required = false) String rangeValue) throws Exception{
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
	
	@RequestMapping("data/v1/trend/current")
    @ResponseBody
	public ProAPIResponse getCurrentTrend(@ModelAttribute FIQLSelector selector, @RequestParam(required = false) String rangeField, @RequestParam(required = false) String rangeValue) throws Exception {
		return getTrends(getCurrentDateAppendedSelector(selector), rangeField, rangeValue);
	}
	
	@RequestMapping("data/v1/trend/hitherto")
    @ResponseBody
	public ProAPIResponse getHithertoTrend(@ModelAttribute FIQLSelector selector, @RequestParam(required = false) String rangeField, @RequestParam(required = false) String rangeValue) throws Exception {
		return getTrends(getHithertoDateAppendedSelector(selector), rangeField, rangeValue);
	}
	
	@RequestMapping("data/v1/price-trend")
    public @ResponseBody
    ProAPIResponse getPriceTrends(@ModelAttribute FIQLSelector selector, @RequestParam(required = false) String rangeField, @RequestParam(required = false) String rangeValue) throws Exception{
		return getTrends(getDominantSupplyAppendedSelector(selector), rangeField, rangeValue);
    }
	
	@RequestMapping("data/v1/price-trend/current")
    public @ResponseBody
    ProAPIResponse getCurrentPriceTrends(@ModelAttribute FIQLSelector selector, @RequestParam(required = false) String rangeField, @RequestParam(required = false) String rangeValue) throws Exception{
		return getTrends(getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector)), rangeField, rangeValue);
    }
	
	@RequestMapping("data/v1/price-trend/hitherto")
    public @ResponseBody
    ProAPIResponse getHithertoPriceTrends(@ModelAttribute FIQLSelector selector, @RequestParam(required = false) String rangeField, @RequestParam(required = false) String rangeValue) throws Exception{
		return getTrends(getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(selector)), rangeField, rangeValue);
    }
	
	
	private FIQLSelector getDominantSupplyAppendedSelector(FIQLSelector selector){
		selector.addAndConditionToFilter("unitType==" + trendService.getDominantSupply(selector));
		selector.addField("unitType");
		return selector;
	}
	
	private FIQLSelector getCurrentDateAppendedSelector(FIQLSelector selector){
		selector.addAndConditionToFilter("month==" + currentMonth);
		return selector;
	}
	
	private FIQLSelector getHithertoDateAppendedSelector(FIQLSelector selector){
		selector.addAndConditionToFilter("month=le=" + currentMonth);
		return selector;
	}
}