package com.proptiger.data.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.exception.NotificationMediumNotFoundException;
import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.repo.NotificationMediumDao;

@Service
public class NotificationMediumService {

    @Autowired
    private NotificationMediumDao notificationMediumDao;

    public NotificationMedium findNotificationMediumByMediumType(MediumType mediumType) {
        List<NotificationMedium> mediums = notificationMediumDao.findByName(mediumType);
        if (mediums == null || mediums.size() != 1) {
            throw new NotificationMediumNotFoundException("Zero or more than one Notification Medium found in DB");
        }
        return mediums.get(0);
    }

}
