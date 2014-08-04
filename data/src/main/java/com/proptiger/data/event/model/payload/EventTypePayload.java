package com.proptiger.data.event.model.payload;

import java.util.Date;
import java.util.List;

import com.proptiger.data.event.model.RawDBEvent;

public abstract class EventTypePayload {
    private String                       transactionKeyName;
    private Object                       transactionId;
    private String                       primaryKeyName;
    private Object                       primaryKeyValue;
    private String                       transactionDateKeyName;
    private Date                         transactionDateKeyValue;

    private List<EventTypeUpdateHistory> eventTypeUpdateHistories;

    public List<EventTypeUpdateHistory> getEventTypeUpdateHistories() {
        return eventTypeUpdateHistories;
    }

    public void setEventTypeUpdateHistories(List<EventTypeUpdateHistory> eventTypeUpdateHistories) {
        this.eventTypeUpdateHistories = eventTypeUpdateHistories;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String idName) {
        this.primaryKeyName = idName;
    }

    public Object getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    public void setPrimaryKeyValue(Object idValue) {
        this.primaryKeyValue = idValue;
    }

    public abstract void populatePayloadValues(RawDBEvent rawDBEvent, String attributeName);

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object id) {
        this.transactionId = id;
    }

    public String getTransactionKeyName() {
        return transactionKeyName;
    }

    public void setTransactionKeyName(String transactionKeyName) {
        this.transactionKeyName = transactionKeyName;
    }

    public String getTransactionDateKeyName() {
        return transactionDateKeyName;
    }

    public void setTransactionDateKeyName(String transactionDateKeyName) {
        this.transactionDateKeyName = transactionDateKeyName;
    }

    public Date getTransactionDateKeyValue() {
        return transactionDateKeyValue;
    }

    public void setTransactionDateKeyValue(Date transactionDateKeyValue) {
        this.transactionDateKeyValue = transactionDateKeyValue;
    }

}
