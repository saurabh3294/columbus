package com.proptiger.data.notification.processor;

import java.util.List;
import java.util.Map;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

public interface NotificationMessageProcessor {

    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadBySubscribedUserList(
            List<ForumUser> userList,
            NotificationTypeGenerated ntGenerated);

    public Map<Integer, NotificationMessagePayload> getNotificationMessagePayloadByUnsubscribedUserList(
            List<ForumUser> unsubscribedUserList,
            NotificationTypeGenerated ntGenerated);

}
