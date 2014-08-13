package com.proptiger.data.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.repo.NotificationMessageDao;

@Service
public class NotificationMessageService {
    @Autowired
    private NotificationMessageDao notificationMessageDao;
}
