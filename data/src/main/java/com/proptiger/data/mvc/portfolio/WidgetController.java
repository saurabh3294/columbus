package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.portfolio.Widget;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.WidgetService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/widget")
public class WidgetController {

	@Autowired
	private WidgetService widgetService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getWidgets(){
		List<Widget> widgets = widgetService.getAllWidgets();
		return new ProAPISuccessCountResponse(widgets, widgets.size());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{widgetId}")
	@ResponseBody
	public ProAPIResponse getWidget(@PathVariable Integer widgetId){
		Widget widget = widgetService.getWidget(widgetId);
		return new ProAPISuccessResponse(widget);
	}
	
}
