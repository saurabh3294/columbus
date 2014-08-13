package com.proptiger.data.notification.enums;

public enum NotificationStatus {
    /*
     * Notification Type Status
     */
    NotificationTypeGenerated, 
    /*
     * Notification Message Status
     */
    NotificationMessageGenerated, NotificationMessageProcessed,
    /*
     * Notification Generated Status
     */
    NotificationGenerated, NotificationScheduled, NotificationMerged, NotificationSuppressed;
}
