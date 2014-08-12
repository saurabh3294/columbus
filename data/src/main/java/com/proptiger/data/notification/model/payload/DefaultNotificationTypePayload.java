package com.proptiger.data.notification.model.payload;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class DefaultNotificationTypePayload extends NotificationTypePayload {

    /**
     * 
     */
    private static final long serialVersionUID = 8340555533239509545L;

    private String            transactionKeyName;
    private Object            transactionId;
    
    private String            transactionDateKeyName;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date              transactionDateKeyValue;

    public String getTransactionKeyName() {
        return transactionKeyName;
    }

    public void setTransactionKeyName(String transactionKeyName) {
        this.transactionKeyName = transactionKeyName;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
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

    @Override
    public void populatePayloadValues() {
        // TODO Auto-generated method stub

    }

}
