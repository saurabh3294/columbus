package com.proptiger.data.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.event.model.EventGenerated;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.repo.NotificationTypeGeneratedDao;

@Service
public class NotificationTypeGenerationService {

    @Autowired
    private NotificationTypeGeneratedDao notificationTypeGeneratedDao;

    public Integer getActiveNotificationTypeCount() {
        return notificationTypeGeneratedDao
                .getNotificationTypeCountByNotificationStatus(NotificationStatus.NotificationTypeGenerated);
    }

    public List<NotificationTypeGenerated> getNotificationTypesForEventGenerated(EventGenerated eventGenerated) {
        return null;
    }

    public void persistNotificationTypes(List<NotificationTypeGenerated> ntGeneratedList) {

    }

}
