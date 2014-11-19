package com.proptiger.data.event.generator.model;

import java.util.List;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.RawEventTableDetails;

public class RawDBEventTableConfig {

    private static final String             DB_OPERATION_ATTRIBUTE_NAME = "_t_operation";

    private RawEventTableDetails            rawEventTableDetails;

    // TODO: Use Map instead of List
    private List<RawDBEventOperationConfig> rawDBEventOperationConfigs;

    public RawDBEventTableConfig(
            RawEventTableDetails rawEventTableDetails,
            List<RawDBEventOperationConfig> rawDBEventOperationConfigs) {
        super();
        this.rawEventTableDetails = rawEventTableDetails;
        this.rawDBEventOperationConfigs = rawDBEventOperationConfigs;
    }

    public RawDBEventTableConfig() {
        super();
    }

    public List<RawDBEventOperationConfig> getRawDBEventOperationConfigs() {
        return rawDBEventOperationConfigs;
    }

    public void setRawDBEventOperationConfigs(List<RawDBEventOperationConfig> dbRawEventOperationConfigs) {
        this.rawDBEventOperationConfigs = dbRawEventOperationConfigs;
    }

    public RawEventTableDetails getRawEventTableDetails() {
        return rawEventTableDetails;
    }

    public void setRawEventTableDetails(RawEventTableDetails rawEventTableDetails) {
        this.rawEventTableDetails = rawEventTableDetails;
    }

    public static String getDbOperationAttributeName() {
        return DB_OPERATION_ATTRIBUTE_NAME;
    }

    public RawDBEventOperationConfig getDbRawEventOperationConfig(DBOperation dbOperation) {
        for (RawDBEventOperationConfig dbRawEventOperationConfig : rawDBEventOperationConfigs) {
            if (dbRawEventOperationConfig.getDbOperation().equals(dbOperation)) {
                return dbRawEventOperationConfig;
            }
        }
        return null;
    }

}
