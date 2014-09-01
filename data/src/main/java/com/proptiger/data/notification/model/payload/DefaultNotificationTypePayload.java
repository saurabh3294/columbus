package com.proptiger.data.notification.model.payload;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.event.model.payload.DefaultEventTypePayload;
import com.proptiger.data.event.model.payload.EventTypePayload;

@Deprecated
public class DefaultNotificationTypePayload extends NotificationTypePayload {

    /**
     * 
     */
    private static final long serialVersionUID = -6327783040553490015L;

    private String            transactionIdName;
    private Object            transactionId;

    private String            transactionDateName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date              transactionDate;

    private Object            oldValue;
    private Object            newValue;

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

    @Override
    public void populatePayloadValues(EventTypePayload eventTypePayload) {
        DefaultEventTypePayload defaultEventTypePayload = (DefaultEventTypePayload) eventTypePayload;
        this.transactionIdName = defaultEventTypePayload.getTransactionKeyName();
        this.transactionId = defaultEventTypePayload.getTransactionId();
        this.transactionDateName = defaultEventTypePayload.getTransactionDateKeyName();
        this.transactionDate = defaultEventTypePayload.getTransactionDateKeyValue();
        this.oldValue = defaultEventTypePayload.getOldValue();
        this.newValue = defaultEventTypePayload.getNewValue();

    }

}
