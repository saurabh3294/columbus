package com.proptiger.data.event.processor.seo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.Status;
import com.proptiger.data.event.enums.EventAllAttributeName;
import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.processor.DBEventProcessor;

@Service
public class SeoLocalityChangeProcessor extends DBEventProcessor {
    private static Logger   logger = LoggerFactory.getLogger(SeoLocalityChangeProcessor.class);

    @Override
    public EventGenerated populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Locality Change Event Type Old data.");
        DefaultEventTypePayload payload = (DefaultEventTypePayload) event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        Class<?> className = newValue.getClass();
                
        /**
         * in case of update event, checking the value.
         */
        if (newValue != null && newValue.getClass().equals(String.class)) {
            if (newValue.equals(Status.Active.name())) {
                return event;
            }
            return null;
        }
        else if(newValue instanceof Map<?,?>){
            Map<String, Object> valuesMap = (Map<String, Object>)newValue;
            if(valuesMap.get("STATUS").equals(Status.Active.name())){
                return event;
            }
        }
                
        return null;
    }
}
