package com.proptiger.data.event.generator.model;

import java.util.Date;
import java.util.List;

import com.proptiger.data.event.model.DBRawEventTableLog;

public class DBRawEventTableConfig {
    private DBRawEventTableLog dbRawEventTableLog;
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

}
