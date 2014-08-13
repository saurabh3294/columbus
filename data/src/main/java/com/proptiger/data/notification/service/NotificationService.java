/**
 * 
 */
package com.proptiger.data.notification.service;

import com.proptiger.data.notification.model.legacy.NotificationMessageOld;

/**
 * @author mandeep
 *
 */
public interface NotificationService {
    public void createNotificationMessage(NotificationMessageOld notificationMessage);
    public void processNotificationMessages(); // handles merging of notifications
    public void scheduleNotifications();
    public void sendNotifications();
}
