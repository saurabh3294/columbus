package com.proptiger.data.enums;

/**
 * 
 * @author azi
 * 
 */
public enum NotificationType {
    TaskDue(1), TaskOverDue(2), DuplicateLead(3), LeadOffered(4), NoBrokerFound("No Broker Found In Resale Marketplace"), SaleSuccessful(
            5, "Resale Lead Marked Sold In Marketplace"), NoBrokerClaimed(6,
            "No Broker Claimed Lead In Resale Marketplace"), AuctionOverWithoutClaim(7, "No Claim Intimation"), MaxLeadCountForBrokerReached(
            8, "Max lead count reached"), TooManyTasksOverDue(9);

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