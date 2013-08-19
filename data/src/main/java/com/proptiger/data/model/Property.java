package com.proptiger.data.model;

import java.util.Map;

public class Property {
    private String id;

    private Map<String, Object> attrMap;

    public Map<String, Object> getAttrMap() {
        return attrMap;
    }
    
    public void setAttrMap(Map<String, Object> attrMap) {
        this.attrMap = attrMap;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
