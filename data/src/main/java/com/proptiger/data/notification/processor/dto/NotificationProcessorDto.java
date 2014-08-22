package com.proptiger.data.notification.processor.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.model.NotificationGenerated;

public class NotificationProcessorDto {
    private int userId;
    private Map<Integer, NotificationByTypeDto>  notificationByTypeDtos = new LinkedHashMap<Integer, NotificationByTypeDto>();
    private Map<Integer, List<NotificationGenerated>> allNotificationGroupByMessageId = new LinkedHashMap<Integer, List<NotificationGenerated>>();
    
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public Map<Integer, NotificationByTypeDto> getNotificationByTypeDtos() {
        return notificationByTypeDtos;
    }
    public void setNotificationByTypeDtos(Map<Integer, NotificationByTypeDto> notificationByTypeDtos) {
        this.notificationByTypeDtos = notificationByTypeDtos;
    }
    public Map<Integer, List<NotificationGenerated>> getAllNotificationGroupByMessageId() {
        return allNotificationGroupByMessageId;
    }
    public void setAllNotificationGroupByMessageId(Map<Integer, List<NotificationGenerated>> allNotificationGroupByMessageId) {
        this.allNotificationGroupByMessageId = allNotificationGroupByMessageId;
    }
    
    
}
