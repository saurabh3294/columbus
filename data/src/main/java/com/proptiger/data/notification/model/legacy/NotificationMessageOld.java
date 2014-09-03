/**
 * 
 */
package com.proptiger.data.notification.model.legacy;

import com.proptiger.data.notification.model.NotificationType;

/**
 * @author mandeep
 *
 */
public class NotificationMessageOld {
    private int id;
    private NotificationType notificationType;
    private NotificationPayloadOld notificationPayload;
    private NotificationUserOld notificationUser;
    private boolean processed;
}
