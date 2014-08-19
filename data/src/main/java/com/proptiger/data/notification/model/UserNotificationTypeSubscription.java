package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.SubscriptionType;

@Entity
@Table(name = "user_notification_type_subscription")
public class UserNotificationTypeSubscription extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -8470633621269549071L;

    @Id
    @Column(name = "id")
    private int               id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private ForumUser         forumUser;

    @Column(name = "notification_type_id")
    private int               notificationTypeId;

    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType  subscriptionType;
    
    @Column(name = "is_deleted")
    @Enumerated(EnumType.ORDINAL)
    private Integer isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ForumUser getForumUser() {
        return forumUser;
    }

    public void setForumUser(ForumUser forumUser) {
        this.forumUser = forumUser;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}
