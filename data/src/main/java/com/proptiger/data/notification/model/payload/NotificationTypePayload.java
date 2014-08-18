package com.proptiger.data.notification.model.payload;

import java.io.Serializable;
import java.util.List;

import com.proptiger.data.event.model.payload.EventTypePayload;

public abstract class NotificationTypePayload implements Serializable {

    /**
     * 
     */
    private static final long                     serialVersionUID = -8078901353675223123L;

    private String                                primaryKeyName;
    private Object                                primaryKeyValue;

    private List<NotificationTypeUpdateHistory> notificationTypeUpdateHistories;

    public abstract void populatePayloadValues(EventTypePayload eventTypePayload);

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

}
