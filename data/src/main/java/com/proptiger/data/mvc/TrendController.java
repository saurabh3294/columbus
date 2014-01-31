package com.proptiger.data.mvc;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.TrendService;

@Controller
@RequestMapping
public class TrendController extends BaseController{
	@Autowired
    private TrendService trendService;
	int x = 0;
	@RequestMapping("data/v2/trend")
    public @ResponseBody
    ProAPIResponse getV2Projects(@ModelAttribute FIQLSelector selector) throws Exception {
        List<InventoryPriceTrend> response = trendService.getTrend(selector);
        return new ProAPISuccessResponse(super.groupFieldsAsPerSelector(response, selector));
    }
}