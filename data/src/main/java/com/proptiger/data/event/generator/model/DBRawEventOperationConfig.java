package com.proptiger.data.event.generator.model;

import java.util.List;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.EventType;

public class DBRawEventOperationConfig {
    private DBOperation dbOperation;
    private List<DBRawEventAttributeConfig> listDBRawEventAttributeConfigs;
    private List<EventType> listEventTypes;
    
    public DBRawEventOperationConfig(
            DBOperation dbOperation,
            List<DBRawEventAttributeConfig> listDBRawEventAttributeConfigs,
            List<EventType> listEventTypes) {
        super();
        this.dbOperation = dbOperation;
        this.listDBRawEventAttributeConfigs = listDBRawEventAttributeConfigs;
        this.listEventTypes = listEventTypes;
    }
    
    public DBRawEventOperationConfig() {
        super();
    }

    public DBOperation getDbOperation() {
        return dbOperation;
    }
    public void setDbOperation(DBOperation dbOperation) {
        this.dbOperation = dbOperation;
    }
    public List<DBRawEventAttributeConfig> getListDBRawEventAttributeConfigs() {
        return listDBRawEventAttributeConfigs;
    }
    public void setListDBRawEventAttributeConfigs(List<DBRawEventAttributeConfig> listDBRawEventAttributeConfigs) {
        this.listDBRawEventAttributeConfigs = listDBRawEventAttributeConfigs;
    }
    public List<EventType> getListEventTypes() {
        return listEventTypes;
    }
    public void setListEventTypes(List<EventType> listEventTypes) {
        this.listEventTypes = listEventTypes;
    }
}
