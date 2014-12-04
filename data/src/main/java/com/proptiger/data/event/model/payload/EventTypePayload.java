package com.proptiger.data.event.model.payload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.event.model.RawDBEvent;

public abstract class EventTypePayload implements Serializable {

    private static final long            serialVersionUID = 6402700328521298042L;

    private String                       transactionKeyName;
    private Object                       transactionId;
    private String                       primaryKeyName;
    private Object                       primaryKeyValue;
    private String                       transactionDateKeyName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date                         transactionDateKeyValue;

    private String                       eventCreatedDateKeyName;
    private Date                         eventCreatedDateKeyValue;

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

    public void populatePayloadValues(RawDBEvent rawDBEvent, String attributeName) {
        return;
    }

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

    public String getEventCreatedDateKeyName() {
        return eventCreatedDateKeyName;
    }

    public void setEventCreatedDateKeyName(String eventCreatedDateKeyName) {
        this.eventCreatedDateKeyName = eventCreatedDateKeyName;
    }

    public Date getEventCreatedDateKeyValue() {
        return eventCreatedDateKeyValue;
    }

    public void setEventCreatedDateKeyValue(Date eventCreatedDateKeyValue) {
        this.eventCreatedDateKeyValue = eventCreatedDateKeyValue;
    }

}
