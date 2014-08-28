package com.proptiger.data.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.SentNotificationLog;
import com.proptiger.data.notification.repo.SentNotificationLogDao;

@Service
public class SentNotificationLogService {

    @Autowired
    private SentNotificationLogDao sentNotificationLogDao;

    public void save(SentNotificationLog sentNotification) {
        sentNotificationLogDao.save(sentNotification);
    }

}
