package com.proptiger.data.event.enums;

public enum EventTypeName {
    ProjectGenerateUrl("project_url_generation"), PropertyGenerateUrl("property_url_generation"), BuilderGenerateUrl("builder_url_generation"),
    LocalityGenerateUrl("locality_url_generation"), SuburbGenerateUrl("suburb_url_generation"), CityGenerateUrl("city_url_generation"),
    ProjectDeleteUrl("project_url_delete"), PropertyDeleteUrl("property_url_delete"), BuilderDeleteUrl("builder_url_delete"),
    LocalityDeleteUrl("locality_url_delete"), SuburbDeleteUrl("suburb_url_delete"), CityDeleteUrl("city_url_delete");
    
    String eventTypeName;

    private EventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }
    
}
