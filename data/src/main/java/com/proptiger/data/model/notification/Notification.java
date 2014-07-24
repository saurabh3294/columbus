package com.proptiger.data.model.notification;

import java.util.Date;

public class Notification {
    private int id;
    private NotificationMessage notificationMessage;
    private NotificationMedium notificationMedium;
    private NotificationStatus status;
    private Date dispatchTime;
}
