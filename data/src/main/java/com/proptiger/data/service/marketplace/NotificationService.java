package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.marketplace.Notification;
import com.proptiger.data.model.marketplace.NotificationType;
import com.proptiger.data.repo.marketplace.NotificationDao;
import com.rits.cloning.Cloner;

/**
 * 
 * @author azi
 * 
 */
@Service
public class NotificationService {
    @Autowired
    private NotificationDao notificationDao;

    /**
     * 
     * @param userId
     * @return {@link List} of {@link Notification} grouped on the basis of
     *         {@link NotificationType} in default order
     */
    public List<NotificationType> getNotificationsForUser(int userId) {
        List<NotificationType> notificationTypes = notificationDao.getNotificationTypesForUser(userId);

        List<NotificationType> finalNotificationTypes = new ArrayList<>();
        for (NotificationType notificationType : notificationTypes) {
            for (Notification notification : notificationType.getNotifications()) {
                notification.setNotificationType(null);
            }

            if (notificationType.isGroupable()) {
                finalNotificationTypes.add(notificationType);
            }
            else {
                for (Notification notification : notificationType.getNotifications()) {
                    Cloner cloner = new Cloner();
                    NotificationType newNotificationType = cloner.deepClone(notificationType);

                    newNotificationType.setNotifications(Arrays.asList(notification));
                    finalNotificationTypes.add(newNotificationType);
                }
            }
        }

        Collections.sort(finalNotificationTypes, NotificationType.getNotificationtypereversecomparator());
        return finalNotificationTypes;
    }
}