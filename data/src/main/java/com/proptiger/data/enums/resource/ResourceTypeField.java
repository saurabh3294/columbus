package com.proptiger.data.enums.resource;

/**
 * @author Rajeev Pandey
 * 
 */
public enum ResourceTypeField {

    NAME("name"), SIZE("size");

    private String type;

    private ResourceTypeField(String t) {
        this.type = t;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
