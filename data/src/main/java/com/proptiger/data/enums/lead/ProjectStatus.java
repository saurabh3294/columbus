package com.proptiger.data.enums.lead;

public enum ProjectStatus {

    UNDERCONSTRUCTION("Under Construction"), CANCELLED("Cancelled"), COMPLETED("Completed"), ONHOLD("On Hold"), 
    NOTLAUNCHED("Not Launched"), LAUNCH("Launch"), PRELAUNCH("Pre Launch");

    private String projectStatus;

    private ProjectStatus(String status) {
        this.projectStatus = status;
    }

}
