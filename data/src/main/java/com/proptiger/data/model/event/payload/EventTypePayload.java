package com.proptiger.data.model.event.payload;

import java.util.List;


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
    
}
