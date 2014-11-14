package com.proptiger.data.event.generator.model;

import java.util.List;

import com.proptiger.data.event.enums.DBOperation;
import com.proptiger.data.event.model.EventType;

public class RawDBEventOperationConfig {
    private DBOperation                     dbOperation;

    // TODO: Use Map instead of List
    private List<RawDBEventAttributeConfig> rawDBEventAttributeConfigs;

    private List<EventType>                 listEventTypes;

    public RawDBEventOperationConfig(
            DBOperation dbOperation,
            List<RawDBEventAttributeConfig> rawDBEventAttributeConfigs,
            List<EventType> listEventTypes) {
        super();
        this.dbOperation = dbOperation;
        this.rawDBEventAttributeConfigs = rawDBEventAttributeConfigs;
        this.listEventTypes = listEventTypes;
    }

    public RawDBEventOperationConfig() {
        super();
    }

    public DBOperation getDbOperation() {
        return dbOperation;
    }

    public void setDbOperation(DBOperation dbOperation) {
        this.dbOperation = dbOperation;
    }

    public List<RawDBEventAttributeConfig> getRawDBEventAttributeConfigs() {
        return rawDBEventAttributeConfigs;
    }

    public void setRawDBEventAttributeConfigs(List<RawDBEventAttributeConfig> rawDBEventAttributeConfigs) {
        this.rawDBEventAttributeConfigs = rawDBEventAttributeConfigs;
    }

    public List<EventType> getListEventTypes() {
        return listEventTypes;
    }

    public void setListEventTypes(List<EventType> listEventTypes) {
        this.listEventTypes = listEventTypes;
    }

    public RawDBEventAttributeConfig getRawDBEventAttributeConfig(String attributeName) {
        if (rawDBEventAttributeConfigs == null) {
            return null;
        }
        for (RawDBEventAttributeConfig rawDBEventAttributeConfig : rawDBEventAttributeConfigs) {
            if (rawDBEventAttributeConfig.getAttributeName().equals(attributeName)) {
                return rawDBEventAttributeConfig;
            }
        }
        return null;
    }
}
