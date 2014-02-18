package com.proptiger.data.service.portfolio;

/**
 * @author Rajeev Pandey
 * 
 */
public enum LeadPageName {
    PORTFOLIO("portfolio"), NO_NAME("");

    private LeadPageName(String s) {
        this.name = s;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
