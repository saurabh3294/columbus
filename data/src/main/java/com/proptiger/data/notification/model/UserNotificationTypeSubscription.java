package com.proptiger.data.notification.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.SubscriptionType;

@Entity
@Table(name = "notification.user_notification_type_subscription")
public class UserNotificationTypeSubscription extends BaseModel {

    private static final long             serialVersionUID = -8470633621269549071L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int                           id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User                          user;

    @Column(name = "notification_type_id")
    private int                           notificationTypeId;

    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType              subscriptionType;

    @OneToMany(mappedBy = "notificationTypeSubscription")
    private List<UserProjectSubscription> userProjectSubscriptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(int notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public List<UserProjectSubscription> getUserProjectSubscriptions() {
        return userProjectSubscriptions;
    }

    public void setUserProjectSubscriptions(List<UserProjectSubscription> userProjectSubscriptions) {
        this.userProjectSubscriptions = userProjectSubscriptions;
    }

}
