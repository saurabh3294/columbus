package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.user.Widget;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.user.WidgetService;

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
    public APIResponse getWidgets() {
        List<Widget> widgets = widgetService.getAllWidgets();
        return new APIResponse(widgets, widgets.size());
    }

    /**
     * this API is used once in launching dashboard, So to test db based url
     * applyig disabled caching.
     * @param widgetId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{widgetId}")
    @ResponseBody
    @DisableCaching
    public APIResponse getWidget(@PathVariable Integer widgetId) {
        Widget widget = widgetService.getWidget(widgetId);
        return new APIResponse(widget);
    }

}
