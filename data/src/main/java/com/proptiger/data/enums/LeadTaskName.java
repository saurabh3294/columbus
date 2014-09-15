package com.proptiger.data.enums;

/**
 * @author Rajeev Pandey
 * 
 */
public enum LeadTaskName {

    Call(1), Email(2), Meeting(3), SiteVisit(4), Negotiation(5);

    private int id;

    private LeadTaskName(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}