/**
 * 
 */
package com.proptiger.data.service.notification;

import com.proptiger.data.model.notification.NotificationMessage;

/**
 * @author mandeep
 *
 */
public interface NotificationService {
    public void createNotificationMessage(NotificationMessage notificationMessage);
    public void processNotificationMessages(); // handles merging of notifications
    public void scheduleNotifications();
    public void sendNotifications();
}
