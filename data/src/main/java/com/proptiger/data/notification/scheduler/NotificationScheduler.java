package com.proptiger.data.notification.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.constants.MediumType;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.service.NotificationGeneratedService;

@Service
public class NotificationScheduler {

    private static Logger                logger      = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationGeneratedService nGeneratedService;

    // Map of userId and medium to priority of Notifications Scheduled
    Map<String, Integer>                 priorityMap = new HashMap<String, Integer>();

    public Integer scheduleNotifications() {
        Integer scheduledNtGeneratedCount = 0;
        List<NotificationGenerated> notificationGeneratedList = nGeneratedService.getRawNotificationGeneratedList();
        notificationGeneratedList = sortNotificationGeneratedByPriority(notificationGeneratedList);

        for (NotificationGenerated nGenerated : notificationGeneratedList) {
            
            Integer userId = nGenerated.getForumUser().getUserId();
            MediumType mediumType = nGenerated.getNotificationMedium().getName();
            Integer lastPriority = priorityMap.get(generatePriorityMapKey(userId, mediumType));
            Integer currentPriority = nGenerated.getNotificationType().getPriority();
            
            if (lastPriority != null && lastPriority < currentPriority) {
                logger.info("Suppressing notificationGeneratedId: " + nGenerated.getId()
                        + " because a higher priority notification was already scheduled/sent.");
                // TODO: Suppress this
            }
            
            NotificationType notificationType = nGenerated.getNotificationType();
            notificationType.getCanReschedule();
            
        }

        return scheduledNtGeneratedCount;
    }

    private List<NotificationGenerated> sortNotificationGeneratedByPriority(List<NotificationGenerated> ngList) {
        return ngList;
    }

    private String generatePriorityMapKey(Integer userId, MediumType medium) {
        return userId + "." + medium.name();
    }
    
    private Date calculateNextScheduleDate(NotificationGenerated nGenerated) {
        NotificationType notificationType = nGenerated.getNotificationType();
        nGeneratedService.getLastScheduledOrSendNotificationGeneratedSameAs(nGenerated);
        nGeneratedService.getLastScheduledOrSentNotificationGeneratedInMediumSameAs(nGenerated);
        notificationType.getFixedDelay();
        notificationType.getFrequencyCycleInSeconds();
        nGenerated.getNotificationMedium().getFrequencyCycleInSeconds();
        return null;
    }

}