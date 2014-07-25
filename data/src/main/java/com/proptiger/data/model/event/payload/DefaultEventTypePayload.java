package com.proptiger.data.model.event.payload;

import java.util.List;
import java.util.Map;

public class DefaultEventTypePayload implements EventTypePayload {

    private String                       oldValue;
    private String                       newValue;
    private Map<String, Object>          idMap;
    private List<EventTypeUpdateHistory> eventTypeUpdateHistories;

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

    public List<EventTypeUpdateHistory> getEventTypeUpdateHistories() {
        return eventTypeUpdateHistories;
    }

    public void setEventTypeUpdateHistories(List<EventTypeUpdateHistory> eventTypeUpdateHistories) {
        this.eventTypeUpdateHistories = eventTypeUpdateHistories;
    }

    public Map<String, Object> getIdMap() {
        return idMap;
    }

    public void setIdMap(Map<String, Object> idMap) {
        this.idMap = idMap;
    }
}
