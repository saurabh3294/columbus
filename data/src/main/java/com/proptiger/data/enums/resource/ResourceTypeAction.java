package com.proptiger.data.enums.resource;

/**
 * @author Rajeev Pandey
 * 
 */
public enum ResourceTypeAction {

    GET("get"), UPDATE("update"), DELETE("delete");

    private String action;

    private ResourceTypeAction(String s) {
        this.action = s;
    }

    public String getAction() {
        return action;
    }

}
