package com.proptiger.data.enums;

/**
 * master list of task statuses
 * 
 * @author azi
 * 
 */
public enum TaskStatus {
    AtsSignedChequeCollected(1), Cancelled(2), ClientNotInterested(3), Done(4), Revised(5), Scheduled(6);

    private int id;

    private TaskStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
