package com.proptiger.data.notification.model.payload;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.event.model.payload.EventTypePayload;

public class DefaultNotificationTypePayload extends NotificationTypePayload {

    /**
     * 
     */
    private static final long serialVersionUID = 8340555533239509545L;

    private String            transactionIdName;
    private Object            transactionId;

    private String            transactionDateName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date              transactionDate;

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
        this.transactionIdName = eventTypePayload.getTransactionKeyName();
        this.transactionId = eventTypePayload.getTransactionId();
        this.transactionDateName = eventTypePayload.getTransactionDateKeyName();
        this.transactionDate = eventTypePayload.getTransactionDateKeyValue();

    }

}
