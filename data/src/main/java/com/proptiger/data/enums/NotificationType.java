package com.proptiger.data.enums;

/**
 * 
 * @author azi
 * 
 */
public enum NotificationType {
    TaskDue(1), TaskOverDue(2), LeadOffered(4), NoBrokerFound("No Broker Found In Resale Marketplace"), SaleSuccessful(
            5, "Resale Lead Marked Sold In Marketplace"), NoBrokerClaimed(6,
            "No Brokecr Claimed Lead In Resale Marketplace");

    private int    id;
    private String emailSubject;

    private NotificationType(int id) {
        this.id = id;
    }

    private NotificationType(int id, String emailSubject) {
        this.id = id;
        this.emailSubject = emailSubject;
    }

    private NotificationType(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public int getId() {
        return id;
    }

    public String getEmailSubject() {
        return this.emailSubject;
    }
}
