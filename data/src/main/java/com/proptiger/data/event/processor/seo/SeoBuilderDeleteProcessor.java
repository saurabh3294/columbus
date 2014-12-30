package com.proptiger.data.event.processor.seo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.data.event.enums.EventAllAttributeName;
import com.proptiger.data.event.processor.DBEventProcessor;

@Service
public class SeoBuilderDeleteProcessor extends DBEventProcessor {
    private static Logger   logger = LoggerFactory.getLogger(SeoBuilderDeleteProcessor.class);

    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Locality Change Event Type Old data.");
        DefaultEventTypePayload payload = (DefaultEventTypePayload) event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        /**
         * in case of update event, checking the value.
         */
        if (newValue != null && newValue.getClass().equals(Integer.class)) {
            Number number = (Number)newValue;
            if (number.intValue() == 1) {
                return event;
            }
            return null;
        }
        else if(newValue instanceof Map<?,?>){
            Map<String, Object> valuesMap = (Map<String, Object>)newValue;
            Number number = (Number)valuesMap.get("builder_status");
            if(number.intValue() == 1){
                return event;
            }
        }
                
        return null;
    }
}
