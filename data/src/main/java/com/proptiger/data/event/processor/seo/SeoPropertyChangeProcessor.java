package com.proptiger.data.event.processor.seo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.EntityType;
import com.proptiger.core.enums.UnitType;
import com.proptiger.core.model.cms.Property;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.service.PropertyService;

@Service
public class SeoPropertyChangeProcessor extends DBEventProcessor{
    private static Logger             logger = LoggerFactory.getLogger(SeoPropertyChangeProcessor.class);
    
    @Autowired
    private PropertyService propertyService;

    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Property Change Event Type Old data.");
        DefaultEventTypePayload payload = (DefaultEventTypePayload)event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        if( newValue.getClass().equals(String.class) ){
            if( !newValue.equals(EntityType.Actual.name()) && !newValue.equals(UnitType.Apartment.name()) && !newValue.equals(UnitType.Villa.name()) && !newValue.equals(UnitType.Plot.name()) ){
                return null;
            }
        }
        Property property = propertyService.getActivePropertyByIdFromDB(Integer.parseInt(event.getEventTypeUniqueKey()));
        // The property is not active.
        if(property == null){
            return null;
        }
        
        return event;
    }
}
