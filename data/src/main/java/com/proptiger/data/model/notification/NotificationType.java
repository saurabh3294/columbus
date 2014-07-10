package com.proptiger.data.model.notification;

import java.util.List;

import com.proptiger.data.model.event.DBEvent;

public class NotificationType {
    private int id;
    private List<DBEvent> events;
    private String schedulePolicy;
    private List<NotificationMedium> notificationMedia;
}
