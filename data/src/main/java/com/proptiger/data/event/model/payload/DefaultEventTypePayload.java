package com.proptiger.data.event.model.payload;

import com.proptiger.data.event.model.RawDBEvent;

public class DefaultEventTypePayload extends EventTypePayload {

    /**
     * 
     */
    private static final long serialVersionUID = -8843513036785607117L;
    
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
        this.oldValue = rawDBEvent.getOldDBValueMap().get(attributeName);
        this.newValue = rawDBEvent.getNewDBValueMap().get(attributeName);
    }

}
