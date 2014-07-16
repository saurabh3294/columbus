package com.proptiger.data.model.event;

public class DBEventType {
    private int id;
    private String tableName;
    private String attrName;
    private Object oldValue;
    private Object newValue;
    private DBOperation dbOperation;
    
    private String schedulePolicy;
}
