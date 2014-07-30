package com.proptiger.data.event.model.payload;

import java.util.List;

import com.proptiger.data.event.model.RawDBEvent;


public abstract class EventTypePayload {
    private String idName;
    private Object idValue;
    
    private List<EventTypeUpdateHistory> eventTypeUpdateHistories;
        
    public List<EventTypeUpdateHistory> getEventTypeUpdateHistories() {
        return eventTypeUpdateHistories;
    }
    public void setEventTypeUpdateHistories(List<EventTypeUpdateHistory> eventTypeUpdateHistories) {
        this.eventTypeUpdateHistories = eventTypeUpdateHistories;
    }
    public String getIdName() {
        return idName;
    }
    public void setIdName(String idName) {
        this.idName = idName;
    }
    public Object getIdValue() {
        return idValue;
    }
    public void setIdValue(Object idValue) {
        this.idValue = idValue;
    }
    
    public abstract void populatePayloadValues(RawDBEvent rawDBEvent, String attributeName);
    
}
