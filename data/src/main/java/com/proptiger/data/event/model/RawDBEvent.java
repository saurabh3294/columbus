package com.proptiger.data.event.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.proptiger.data.event.generator.model.DBRawEventOperationConfig;
import com.proptiger.data.model.BaseModel;

/**
 * 
 * @author sahil
 * 
 */
public class RawDBEvent extends BaseModel{
    
    private DBRawEventTableLog        dbRawEventTableLog;
    private DBRawEventOperationConfig dbRawEventOperationConfig;
    private Map<String, Object>       oldDBValueMap = new HashMap<>();
    private Map<String, Object>       newDBValueMap = new HashMap<>();
    private Object                    primaryKeyValue;
    private Object                    transactionKeyValue;
    @Temporal(TemporalType.TIMESTAMP)
    private Date                      transactionDate;

    public DBRawEventTableLog getDbRawEventTableLog() {
        return dbRawEventTableLog;
    }

    public void setDbRawEventTableLog(DBRawEventTableLog dbRawEventTableLog) {
        this.dbRawEventTableLog = dbRawEventTableLog;
    }

    public DBRawEventOperationConfig getDbRawEventOperationConfig() {
        return dbRawEventOperationConfig;
    }

    public void setDbRawEventOperationConfig(DBRawEventOperationConfig dbRawEventOperationConfig) {
        this.dbRawEventOperationConfig = dbRawEventOperationConfig;
    }

    public Map<String, Object> getOldDBValueMap() {
        return oldDBValueMap;
    }

    public void setOldDBValueMap(Map<String, Object> oldDBValueMap) {
        this.oldDBValueMap = oldDBValueMap;
    }

    public Map<String, Object> getNewDBValueMap() {
        return newDBValueMap;
    }

    public void setNewDBValueMap(Map<String, Object> newDBValueMap) {
        this.newDBValueMap = newDBValueMap;
    }

    public Object getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    public void setPrimaryKeyValue(Object primaryKeyValue) {
        this.primaryKeyValue = primaryKeyValue;
    }

    public Object getTransactionKeyValue() {
        return transactionKeyValue;
    }

    public void setTransactionKeyValue(Object transactionKeyValue) {
        this.transactionKeyValue = transactionKeyValue;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

}
