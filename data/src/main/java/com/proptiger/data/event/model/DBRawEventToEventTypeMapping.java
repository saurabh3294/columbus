package com.proptiger.data.event.model;

import com.proptiger.data.event.enums.DBOperation;

public class DBRawEventToEventTypeMapping {
    
    private int id;
    private String host;
    private String dbName;
    private String tableName;
    private String primaryKeyName;
    private DBOperation dbOperation;
    private String attributeName;
    private EventType eventType;
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
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
    
    public DBOperation getDbOperation() {
        return dbOperation;
    }
    
    public void setDbOperation(DBOperation dbOperation) {
        this.dbOperation = dbOperation;
    }
    
    public String getAttributeName() {
        return attributeName;
    }
    
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    
    
}
