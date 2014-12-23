package com.proptiger.data.event.generator;

public enum EventTypeEnum {
    ProjectGenerateUrl("project_url_generation"), PropertyGenerateUrl("property_url_generation"), BuilderGenerateUrl("builder_url_generation"),
    LocalityGenerateUrl("locality_url_generation"), SuburbGenerateUrl("suburb_url_generation"), CityGenerateUrl("city_url_generation"),
    ProjectDeleteUrl("project_url_delete"), PropertyDeleteUrl("property_url_delete"), BuilderDeleteUrl("builder_url_delete"),
    LocalityDeleteUrl("locality_url_delete"), SuburbDeleteUrl("suburb_url_delete"), CityDeleteUrl("city_url_delete");
    
    String name;

    private EventTypeEnum(String eventTypeName) {
        this.name = eventTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String eventTypeName) {
        this.name = eventTypeName;
    }
    
}
