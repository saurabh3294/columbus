package com.proptiger.data.enums;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Integer> getAllIds() {
        List<Integer> list = new ArrayList<>();
        for (LeadTaskName name : LeadTaskName.values()) {
            list.add(name.getId());
        }
        return list;
    }
}