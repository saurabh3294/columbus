package com.proptiger.data.notification.model.external;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.enums.SubscriptionType;

public class NotificationSubscriptionRequest extends BaseModel {

    private static final long          serialVersionUID  = -3457828164497289239L;

    @Size(min = 1)
    private List<NotificationTypeEnum> notificationTypes = new ArrayList<NotificationTypeEnum>();

    @Size(min = 1)
    private List<User>                 users             = new ArrayList<User>();

    @NotNull
    private SubscriptionType           subscriptionType;

    public NotificationSubscriptionRequest() {

    }

    public NotificationSubscriptionRequest(
            User user,
            List<NotificationTypeEnum> notificationTypes,
            SubscriptionType subscriptionType) {
        this.users.add(user);
        this.notificationTypes.addAll(notificationTypes);
        this.subscriptionType = subscriptionType;
    }

    public List<NotificationTypeEnum> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(List<NotificationTypeEnum> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

}
