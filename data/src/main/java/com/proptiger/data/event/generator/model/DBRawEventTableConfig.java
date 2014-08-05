package com.proptiger.data.event.generator.model;

import java.util.List;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.DBRawEventTableLog;

public class DBRawEventTableConfig {
    
    private static final String DB_OPERATION_ATTRIBUTE_NAME = "_t_operation";
    
    private DBRawEventTableLog dbRawEventTableLog;
    
    // TODO: Use Map instead of List
    private List<DBRawEventOperationConfig> dbRawEventOperationConfigs;

    public DBRawEventTableConfig(
            DBRawEventTableLog dbRawEventTableLog,
            List<DBRawEventOperationConfig> dbRawEventOperationConfigs) {
        super();
        this.dbRawEventTableLog = dbRawEventTableLog;
        this.dbRawEventOperationConfigs = dbRawEventOperationConfigs;
    }

    public DBRawEventTableConfig() {
        super();
    }

    public List<DBRawEventOperationConfig> getDbRawEventOperationConfigs() {
        return dbRawEventOperationConfigs;
    }

    public void setDbRawEventOperationConfigs(List<DBRawEventOperationConfig> dbRawEventOperationConfigs) {
        this.dbRawEventOperationConfigs = dbRawEventOperationConfigs;
    }

    public DBRawEventTableLog getDbRawEventTableLog() {
        return dbRawEventTableLog;
    }

    public void setDbRawEventTableLog(DBRawEventTableLog dbRawEventTableLog) {
        this.dbRawEventTableLog = dbRawEventTableLog;
    }

    public static String getDbOperationAttributeName() {
        return DB_OPERATION_ATTRIBUTE_NAME;
    }
    
    public DBRawEventOperationConfig getDbRawEventOperationConfig(DBOperation dbOperation) {
        for (DBRawEventOperationConfig dbRawEventOperationConfig : dbRawEventOperationConfigs) {
            if (dbRawEventOperationConfig.getDbOperation().equals(dbOperation)) {
                return dbRawEventOperationConfig;
            }
        }
        return null;
    }
    
}
