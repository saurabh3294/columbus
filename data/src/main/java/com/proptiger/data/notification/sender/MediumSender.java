package com.proptiger.data.notification.sender;

public interface MediumSender {
    public boolean send(String template, Integer userId, String notificationTypeName);
}
