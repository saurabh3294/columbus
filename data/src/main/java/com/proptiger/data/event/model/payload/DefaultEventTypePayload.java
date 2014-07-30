package com.proptiger.data.event.model.payload;

import java.util.Map;

import com.proptiger.data.event.model.RawDBEvent;

public class DefaultEventTypePayload extends EventTypePayload {

    private Object oldValue;
    private Object newValue;

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public void populatePayloadValues(RawDBEvent rawDBEvent, String attributeName) {
        // TODO: populate payload with appropriate old and new values
        Map<String, Object> rawEventDBValueMap = rawDBEvent.getDbValueMap();
        this.oldValue = rawEventDBValueMap.get(attributeName);
        this.newValue = rawEventDBValueMap.get(attributeName);
    }

}
