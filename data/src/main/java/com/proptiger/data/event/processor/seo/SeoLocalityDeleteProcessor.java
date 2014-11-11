package com.proptiger.data.event.processor.seo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proptiger.core.enums.Status;
import com.proptiger.data.event.enums.EventAllAttributeName;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.processor.DBEventProcessor;

public class SeoLocalityDeleteProcessor extends DBEventProcessor {
    private static Logger   logger = LoggerFactory.getLogger(SeoLocalityDeleteProcessor.class);

    @Override
    public boolean populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Locality Change Event Type Old data.");
        DefaultEventTypePayload payload = (DefaultEventTypePayload) event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        /**
         * in case of update event, checking the value.
         */
        if (newValue != null && newValue.getClass().equals(String.class)) {
            if (newValue.equals(Status.Inactive.name())) {
                return true;
            }
            return false;
        }
        else if(newValue.getClass().equals(Map.class)){
            Map<String, Map<String, Object>> valuesMap = (Map<String, Map<String, Object>>)newValue;
            Map<String, Object> values = valuesMap.get(EventAllAttributeName.All);
            if(values.get("status").equals(Status.Inactive)){
                return true;
            }
        }
                
        return false;
    }
}
