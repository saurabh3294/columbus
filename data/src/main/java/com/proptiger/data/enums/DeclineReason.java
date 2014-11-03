package com.proptiger.data.enums;

public enum DeclineReason {
    other(99);
    
    private int declineReasonId;
    
    private DeclineReason(int declineReasonId) {
        this.declineReasonId = declineReasonId;
    }

    public int getDeclineReasonId() {
        return declineReasonId;
    }

    public void setDeclineReasonId(int declineReasonId) {
        this.declineReasonId = declineReasonId;
    }
   
}
