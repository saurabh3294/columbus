package com.proptiger.data.notification.model.payload;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.core.event.model.payload.NewsEventTypePayload;
import com.proptiger.core.model.BaseModel;

public class NotificationTypePayload extends BaseModel {

    private static final long                   serialVersionUID                = -8078901353675223123L;

    private String                              primaryKeyName;
    private Object                              primaryKeyValue;

    private String                              transactionIdName;
    private Object                              transactionId;

    private String                              transactionDateName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date                                transactionDate;

    private Object                              oldValue;
    private Object                              newValue;

    private List<NotificationTypePayload>       childNotificationTypePayloads;
    private List<NotificationTypeUpdateHistory> notificationTypeUpdateHistories = new ArrayList<NotificationTypeUpdateHistory>();

    public static NotificationTypePayload newInstance(NotificationTypePayload payload) {
        NotificationTypePayload newPayload = new NotificationTypePayload();
        newPayload.setPrimaryKeyName(payload.getPrimaryKeyName());
        newPayload.setPrimaryKeyValue(payload.getPrimaryKeyValue());
        newPayload.setTransactionIdName(payload.getTransactionIdName());
        newPayload.setTransactionId(payload.getTransactionId());
        newPayload.setTransactionDateName(payload.getTransactionDateName());
        newPayload.setTransactionDate(payload.getTransactionDate());
        newPayload.setOldValue(payload.getOldValue());
        newPayload.setNewValue(payload.getNewValue());
        newPayload.setNotificationTypeUpdateHistories(payload.getNotificationTypeUpdateHistories());

        if (payload.getChildNotificationTypePayloads() != null) {
            List<NotificationTypePayload> childPayloads = new ArrayList<NotificationTypePayload>();
            for (NotificationTypePayload childPayload : payload.getChildNotificationTypePayloads()) {
                childPayloads.add(newInstance(childPayload));
            }
            newPayload.setChildNotificationTypePayloads(childPayloads);
        }
        return newPayload;
    }

    public void populatePayloadValues(EventTypePayload eventTypePayload) {
        this.transactionIdName = eventTypePayload.getTransactionKeyName();
        this.transactionId = eventTypePayload.getTransactionId();
        this.transactionDateName = eventTypePayload.getTransactionDateKeyName();
        this.transactionDate = eventTypePayload.getTransactionDateKeyValue();
        this.primaryKeyName = eventTypePayload.getPrimaryKeyName();
        this.primaryKeyValue = eventTypePayload.getPrimaryKeyValue();
    }

    public void populatePayloadValues(DefaultEventTypePayload eventTypePayload) {
        populatePayloadValues((EventTypePayload) eventTypePayload);
        this.oldValue = eventTypePayload.getOldValue();
        this.newValue = eventTypePayload.getNewValue();
    }

    public void populatePayloadValues(NewsEventTypePayload eventTypePayload) {
        populatePayloadValues((EventTypePayload) eventTypePayload);
        this.extraAttributes.put("post_id", eventTypePayload.getPostId().toString());
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

    public List<NotificationTypePayload> getChildNotificationTypePayloads() {
        return childNotificationTypePayloads;
    }

    public void setChildNotificationTypePayloads(List<NotificationTypePayload> childNotificationTypePayloads) {
        this.childNotificationTypePayloads = childNotificationTypePayloads;
    }

}
