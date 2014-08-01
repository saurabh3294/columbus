package com.proptiger.data.event.generator.model;

import java.util.List;

import com.proptiger.data.event.model.EventType;

public class DBRawEventAttributeConfig {
    private String attributeName;
    private List<EventType> listEventTypes;
    
    public DBRawEventAttributeConfig() {
        super();
    }
    public DBRawEventAttributeConfig(String attributeName, List<EventType> listEventTypes) {
        super();
        this.attributeName = attributeName;
        this.listEventTypes = listEventTypes;
    }
    
    public String getAttributeName() {
        return attributeName;
    }
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    public List<EventType> getListEventTypes() {
        return listEventTypes;
    }
    public void setListEventTypes(List<EventType> listEventTypes) {
        this.listEventTypes = listEventTypes;
    }
}
