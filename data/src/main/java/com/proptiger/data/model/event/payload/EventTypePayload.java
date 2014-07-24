package com.proptiger.data.model.event.payload;

import java.util.List;

import com.proptiger.data.model.event.DBOperation;

public abstract class EventTypePayload {
    private String oldValue;
    private String newValue;
    private Object id;
    private DBOperation dbOperation;
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
    public Object getId() {
        return id;
    }
    public void setId(Object id) {
        this.id = id;
    }
    public DBOperation getDbOperation() {
        return dbOperation;
    }
    public void setDbOperation(DBOperation dbOperation) {
        this.dbOperation = dbOperation;
    }
    public List<EventTypeUpdateHistory> getEventTypeUpdateHistories() {
        return eventTypeUpdateHistories;
    }
    public void setEventTypeUpdateHistories(List<EventTypeUpdateHistory> eventTypeUpdateHistories) {
        this.eventTypeUpdateHistories = eventTypeUpdateHistories;
    }
    
}
