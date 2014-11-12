package com.proptiger.data.event.model.payload.dto;

import java.io.Serializable;

public class EventTypePayloadDataDto implements Serializable {

    public EventTypePayloadDataDto(String attributeName, Object oldValue, Object newValue) {
        super();
        this.attributeName = attributeName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -1848498640075970431L;

    String                    attributeName;
    Object                    oldValue;
    Object                    newValue;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

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
}
