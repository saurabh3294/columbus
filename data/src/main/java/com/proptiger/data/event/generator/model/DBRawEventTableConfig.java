package com.proptiger.data.event.generator.model;

import java.util.List;

public class DBRawEventTableConfig {
    private int               id;
    private String            hostName;
    private String            dbName;
    private String            tableName;
    private String            primaryKeyName;
    private String            transactionKeyName;
    private String            dateAttributeName;
    private String            dateAttributeValue;
    private List<DBRawEventOperationConfig> dbRawEventOperationConfigs;

    public int getId() {
        return id;
    }

    public DBRawEventTableConfig() {
        super();
    }

    public DBRawEventTableConfig(
            String hostName,
            String dbName,
            String tableName,
            String primaryKeyName,
            String transactionKeyName,
            List<DBRawEventOperationConfig> dbRawEventOperationConfigs) {
        super();
        this.hostName = hostName;
        this.dbName = dbName;
        this.tableName = tableName;
        this.primaryKeyName = primaryKeyName;
        this.transactionKeyName = transactionKeyName;
        this.dbRawEventOperationConfigs = dbRawEventOperationConfigs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public String getTransactionKeyName() {
        return transactionKeyName;
    }

    public void setTransactionKeyName(String transactionKeyName) {
        this.transactionKeyName = transactionKeyName;
    }

    public List<DBRawEventOperationConfig> getDbRawEventOperationConfigs() {
        return dbRawEventOperationConfigs;
    }

    public void setDbRawEventOperationConfigs(List<DBRawEventOperationConfig> dbRawEventOperationConfigs) {
        this.dbRawEventOperationConfigs = dbRawEventOperationConfigs;
    }

    public String getDateAttributeName() {
        return dateAttributeName;
    }

    public void setDateAttributeName(String dateAttributeName) {
        this.dateAttributeName = dateAttributeName;
    }

    public String getDateAttributeValue() {
        return dateAttributeValue;
    }

    public void setDateAttributeValue(String dateAttributeValue) {
        this.dateAttributeValue = dateAttributeValue;
    }
    
}
