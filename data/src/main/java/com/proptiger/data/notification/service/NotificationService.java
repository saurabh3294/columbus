/**
 * 
 */
package com.proptiger.data.notification.service;

import com.proptiger.data.notification.model.NotificationMessage;

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
