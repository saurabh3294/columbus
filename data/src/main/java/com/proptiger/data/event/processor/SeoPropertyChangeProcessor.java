package com.proptiger.data.event.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.model.cms.Property;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.service.PropertyService;

public class SeoPropertyChangeProcessor extends DBEventProcessor{
    private static Logger             logger = LoggerFactory.getLogger(SeoPropertyChangeProcessor.class);
    
    @Autowired
    private PropertyService propertyService;

    @Override
    public boolean populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Property Change Event Type Old data.");
        
        Property property = propertyService.getActivePropertyByIdFromDB(Integer.parseInt(event.getEventTypeUniqueKey()));
        // The property is not active.
        if(property == null){
            return false;
        }
        
        return true;
    }
}
