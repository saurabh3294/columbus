/**
 * 
 */
package com.proptiger.data.notification.model;

/**
 * @author mandeep
 *
 */
public class NotificationMessage {
    private int id;
    private NotificationType notificationType;
    private NotificationPayload notificationPayload;
    private NotificationUser notificationUser;
    private boolean processed;
}
