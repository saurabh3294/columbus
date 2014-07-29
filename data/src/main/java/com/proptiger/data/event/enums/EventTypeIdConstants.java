package com.proptiger.data.event.enums;

public enum EventTypeIdConstants {
    PropertyId("propertyId");
    
    private String name;
    private EventTypeIdConstants(String name) {
        this.name = name;
        // TODO Auto-generated constructor stub
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
