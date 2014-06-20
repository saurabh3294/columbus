package com.proptiger.data.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author azi
 * 
 */
public enum ConstructionStatus {
    ReadyForPossession("Ready for Possession"), Occupied("Occupied"), UnderConstruction("Under Construction"), Cancelled(
            "Cancelled"), OnHold("On Hold"), NotLaunched("Not Launched"), Launch("Launch"), PreLaunch("Pre Launch"), Completed("Completed");

    private String                                       status;
    private static final Map<String, ConstructionStatus> lookup = new HashMap<>();

    static {
        for (ConstructionStatus status : ConstructionStatus.values()) {
            lookup.put(status.getStatus(), status);
        }
    }

    private ConstructionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ConstructionStatus fromStringStatus(String constructionStatus) {
        return lookup.get(constructionStatus);
    }
}
