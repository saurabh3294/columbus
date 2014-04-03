package com.proptiger.data.util;

/**
 * @author Rajeev Pandey
 * 
 */
public enum ResourceType {
    LISTING("property listing"), DASHBOARD("dashboard"), WIDGET("widget"), PRICE_TREND("price trend"), BANK("bank"), RESOURCE(
            "resource"), PROJECT_PAYMENT_SCHEDULE("project payment schedule"), PROJECT("project"), LOCALITY("locality"), BUILDER(
            "builder"), AGENT("Agent"), CITY("city"), PROPERTY("property"), USER("user"), SUBURB("suburb");

    private String type;

    private ResourceType(String t) {
        this.type = t;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
