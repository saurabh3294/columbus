package com.proptiger.data.service.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.data.model.user.Widget;
import com.proptiger.data.repo.user.WidgetDao;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * @author Rajeev Pandey
 * 
 */
@Component
public class WidgetService {

    private static Logger logger = LoggerFactory.getLogger(WidgetService.class);

    @Autowired
    private WidgetDao     widgetDao;

    public List<Widget> getAllWidgets() {
        List<Widget> result = widgetDao.findAll();
        return result;
    }

    public Widget getWidget(Integer widgetId) {
        logger.debug("Finding widget id {}", widgetId);
        Widget result = widgetDao.findOne(widgetId);
        if (result == null) {
            logger.error("Widget id {} not found for userid {}", widgetId);
            throw new ResourceNotAvailableException(ResourceType.WIDGET, ResourceTypeAction.GET);
        }
        return result;
    }
}
