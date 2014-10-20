/**
 * 
 */
package com.proptiger.data.notification.model.legacy;


/**
 * @author mandeep
 *
 */
public interface NotificationServiceOld {
    public void createNotificationMessage(NotificationMessageOld notificationMessage);
    public void processNotificationMessages(); // handles merging of notifications
    public void scheduleNotifications();
    public void sendNotifications();
}
