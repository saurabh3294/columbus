package com.proptiger.data.model.event.payload;

import java.util.List;
import java.util.Map;


public abstract class EventTypePayload {
    private Map<String, Object>          idMap;
    private List<EventTypeUpdateHistory> eventTypeUpdateHistories;
    private String uniqueKeyString;
    
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
    public String getUniqueKeyString() {
        return uniqueKeyString;
    }
    public void setUniqueKeyString(String uniqueKeyString) {
        this.uniqueKeyString = uniqueKeyString;
    }
}
