package com.proptiger.data.notification.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.repo.NotificationGeneratedDao;

@Service
public class NotificationGeneratedService {
    
    @Autowired
    private NotificationGeneratedDao notificationGeneratedDao;
    
    public List<NotificationGenerated> getScheduledAndNonExpiredNotifications(){
        return notificationGeneratedDao.findByStatusAndExpiryTimeLessThan(NotificationStatus.NotificationScheduled, new Date());
    }
}
