package com.proptiger.data.model.event.payload;

import java.util.List;
import java.util.Map;


public abstract class EventTypePayload {
    private Map<String, Object>          idMap;
    private List<EventTypeUpdateHistory> eventTypeUpdateHistories;
        
    public Map<String, Object> getIdMap() {
        return idMap;
    }
    public void setIdMap(Map<String, Object> idMap) {
        this.idMap = idMap;
    }
    public List<EventTypeUpdateHistory> getEventTypeUpdateHistories() {
        return eventTypeUpdateHistories;
    }
    public void setEventTypeUpdateHistories(List<EventTypeUpdateHistory> eventTypeUpdateHistories) {
        this.eventTypeUpdateHistories = eventTypeUpdateHistories;
    }
    
}
