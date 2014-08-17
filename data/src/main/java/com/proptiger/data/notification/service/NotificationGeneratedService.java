package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationMessageUpdateHistory;
import com.proptiger.data.notification.repo.NotificationGeneratedDao;

@Service
public class NotificationGeneratedService {

    @Autowired
    private NotificationGeneratedDao notificationGeneratedDao;

    public List<NotificationGenerated> getScheduledAndNonExpiredNotifications() {
        return notificationGeneratedDao.findByStatusAndExpiryTimeLessThan(NotificationStatus.Scheduled, new Date());
    }

    public Map<Integer, List<NotificationGenerated>> groupNotificationGeneratedByuser(
            List<NotificationGenerated> notificationGeneratedList) {
        if (notificationGeneratedList == null) {
            return new HashMap<Integer, List<NotificationGenerated>>();
        }

        Map<Integer, List<NotificationGenerated>> groupNotificationMessageMap = new HashMap<Integer, List<NotificationGenerated>>();
        Integer userId = null;
        List<NotificationGenerated> groupNotifcationGenerated = null;
        for (NotificationGenerated notificationGenerated : notificationGeneratedList) {
            userId = notificationGenerated.getForumUser().getUserId();
            groupNotifcationGenerated = groupNotificationMessageMap.get(userId);

            if (groupNotificationMessageMap.get(userId) == null) {
                groupNotifcationGenerated = new ArrayList<NotificationGenerated>();
            }
            groupNotifcationGenerated.add(notificationGenerated);
            groupNotificationMessageMap.put(userId, groupNotifcationGenerated);
        }

        return groupNotificationMessageMap;
    }

    public Map<String, List<NotificationGenerated>> groupNotificationsByNotificationType(
            List<NotificationGenerated> notificationGeneratedList) {
        if (notificationGeneratedList == null) {
            return new HashMap<String, List<NotificationGenerated>>();
        }

        Map<String, List<NotificationGenerated>> groupNotificationMessageMap = new HashMap<String, List<NotificationGenerated>>();
        NotificationType notificationType = null;
        String notificationName = null;
        List<NotificationGenerated> groupNotifcationMessage = null;
        for (NotificationGenerated notificationGenerated : notificationGeneratedList) {

            notificationType = notificationGenerated.getNotificationType();
            notificationName = notificationType.getName();
            groupNotifcationMessage = groupNotificationMessageMap.get(notificationName);

            if (groupNotificationMessageMap.get(notificationName) == null) {
                groupNotifcationMessage = new ArrayList<NotificationGenerated>();
            }
            groupNotifcationMessage.add(notificationGenerated);
            groupNotificationMessageMap.put(notificationName, groupNotifcationMessage);
        }

        return groupNotificationMessageMap;
    }

    public void addNotificationGeneratedUpdateHistory(
            NotificationGenerated notificationGenerated,
            NotificationStatus notificationStatus) {
        NotificationMessageUpdateHistory nHistory = new NotificationMessageUpdateHistory(notificationStatus, new Date());

        notificationGenerated.getNotificationMessagePayload().getNotificationMessageUpdateHistories().add(nHistory);
    }
}
