/**
 * 
 */
package com.proptiger.data.notification.model.legacy;

import com.proptiger.data.notification.model.NotificationType;

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
