package com.proptiger.data.notification.sender;

import com.proptiger.data.model.ForumUser;

public interface MediumSender {
    public void send(String template, ForumUser forumUser, String notificationTypeName);
}
