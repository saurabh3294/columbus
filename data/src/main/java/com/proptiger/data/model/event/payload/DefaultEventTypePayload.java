package com.proptiger.data.model.event.payload;

public class DefaultEventTypePayload extends EventTypePayload {

    private String oldValue;
    private String newValue;

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

}
