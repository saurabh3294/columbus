package com.proptiger.data.notification.processor.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class NotificationIntraProcessorDto {
    private int userId;
    private Map<Integer, NotificationByTypeDto>  notificationByTypeDtos = new LinkedHashMap<Integer, NotificationByTypeDto>();
    
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
    
    
}
