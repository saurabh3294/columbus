package com.proptiger.data.notification.schedular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.service.NotificationGeneratedService;

public class NotificationSchedular {

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;

    // Map of userId and medium to priority of Notifications Scheduled
    Map<String, Integer> priorityMap = new HashMap<String, Integer>();
    
    public Integer scheduleNotifications() {
        Integer scheduledNtGeneratedCount = 0;
        List<NotificationGenerated> notificationGeneratedList = new ArrayList<NotificationGenerated>();
        notificationGeneratedList = sortNotificationGeneratedByPriority(notificationGeneratedList);

        for (NotificationGenerated nGenerated : notificationGeneratedList) {
            
        }
        
        return scheduledNtGeneratedCount;
    }

    private List<NotificationGenerated> sortNotificationGeneratedByPriority(List<NotificationGenerated> ngList) {
        return ngList;
    }

}