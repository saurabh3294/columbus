package com.proptiger.data.notification.model.payload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.model.payload.EventTypePayload;

public class NotificationTypePayload implements Serializable {

    /**
     * 
     */
    private static final long                   serialVersionUID = -8078901353675223123L;

    private String                              primaryKeyName;
    private Object                              primaryKeyValue;

    private String                              transactionIdName;
    private Object                              transactionId;

    private String                              transactionDateName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date                                transactionDate;

    private Object                              oldValue;
    private Object                              newValue;

    private List<NotificationTypeUpdateHistory> notificationTypeUpdateHistories;

    public void populatePayloadValues(EventTypePayload eventTypePayload){
        DefaultEventTypePayload defaultEventTypePayload = (DefaultEventTypePayload) eventTypePayload;
        this.transactionIdName = defaultEventTypePayload.getTransactionKeyName();
        this.transactionId = defaultEventTypePayload.getTransactionId();
        this.transactionDateName = defaultEventTypePayload.getTransactionDateKeyName();
        this.transactionDate = defaultEventTypePayload.getTransactionDateKeyValue();
        this.oldValue = defaultEventTypePayload.getOldValue();
        this.newValue = defaultEventTypePayload.getNewValue();
    }

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

    public List<NotificationTypeUpdateHistory> getNotificationTypeUpdateHistories() {
        return notificationTypeUpdateHistories;
    }

    public void setNotificationTypeUpdateHistories(List<NotificationTypeUpdateHistory> notificationTypeUpdateHistories) {
        this.notificationTypeUpdateHistories = notificationTypeUpdateHistories;
    }

    public void addNotificationTypeUpdateHistory(NotificationTypeUpdateHistory updateHistory) {
        this.getNotificationTypeUpdateHistories().add(updateHistory);
    }

    public String getTransactionIdName() {
        return transactionIdName;
    }

    public void setTransactionIdName(String transactionIdName) {
        this.transactionIdName = transactionIdName;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDateName() {
        return transactionDateName;
    }

    public void setTransactionDateName(String transactionDateName) {
        this.transactionDateName = transactionDateName;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
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
