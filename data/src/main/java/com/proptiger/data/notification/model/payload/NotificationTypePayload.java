package com.proptiger.data.notification.model.payload;

import java.io.Serializable;
import java.util.List;

import com.proptiger.data.event.model.payload.EventTypeUpdateHistory;

public abstract class NotificationTypePayload implements Serializable {

    /**
     * 
     */
    private static final long            serialVersionUID = -8078901353675223123L;

    private String                       primaryKeyName;
    private Object                       primaryKeyValue;

    private List<EventTypeUpdateHistory> eventTypeUpdateHistories;

    public abstract void populatePayloadValues();

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public Object getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    public void setPrimaryKeyValue(Object primaryKeyValue) {
        this.primaryKeyValue = primaryKeyValue;
    }

    public List<EventTypeUpdateHistory> getEventTypeUpdateHistories() {
        return eventTypeUpdateHistories;
    }

    public void setEventTypeUpdateHistories(List<EventTypeUpdateHistory> eventTypeUpdateHistories) {
        this.eventTypeUpdateHistories = eventTypeUpdateHistories;
    }

}
