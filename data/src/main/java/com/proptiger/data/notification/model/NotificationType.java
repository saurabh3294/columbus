package com.proptiger.data.notification.model;

import java.util.List;

import com.proptiger.data.event.model.EventType;

public class NotificationType {
    private int id;
    private List<EventType> events;
    private String schedulePolicy;
   
}
