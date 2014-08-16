package com.proptiger.data.notification.processor.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class NotificationIntraProcessorDto {
    private int userId;
    private Map<String, NotificationByTypeDto>  notificationByTypeDtos = new LinkedHashMap<String, NotificationByTypeDto>();
    
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public Map<String, NotificationByTypeDto> getNotificationByTypeDtos() {
        return notificationByTypeDtos;
    }
    public void setNotificationByTypeDtos(Map<String, NotificationByTypeDto> notificationByTypeDtos) {
        this.notificationByTypeDtos = notificationByTypeDtos;
    }
    
    
}
