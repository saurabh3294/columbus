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
public class SeoBuilderChangeProcessor extends DBEventProcessor {
    private static Logger   logger = LoggerFactory.getLogger(SeoBuilderChangeProcessor.class);

    @Override
    public boolean populateEventSpecificData(EventGenerated event) {
        logger.info(" Populating the Locality Change Event Type Old data.");
        DefaultEventTypePayload payload = (DefaultEventTypePayload) event.getEventTypePayload();
        Object newValue = payload.getNewValue();
        /**
         * in case of update event, checking the value.
         */
        if (newValue != null && newValue.getClass().equals(Double.class)) {
            Number number = (Number)newValue;
            if (number.intValue() == 0) {
                return true;
            }
            return false;
        }
        else if(newValue.getClass().equals(Map.class)){
            Map<String, Map<String, Object>> valuesMap = (Map<String, Map<String, Object>>)newValue;
            Map<String, Object> values = valuesMap.get(EventAllAttributeName.All);
            Number number = (Number)values.get("builder_status");
            if(number.intValue() == 0){
                return true;
            }
        }
                
        return false;
    }
}
