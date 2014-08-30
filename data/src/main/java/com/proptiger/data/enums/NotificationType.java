package com.proptiger.data.enums;

/**
 * 
 * @author azi
 * 
 */
public enum NotificationType {
    TaskDue(1), TaskOverDue(2);

    private int id;

    private NotificationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
