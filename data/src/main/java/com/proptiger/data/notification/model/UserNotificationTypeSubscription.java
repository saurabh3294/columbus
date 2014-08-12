package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;

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
    private int               notification_type_id;

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

    public int getNotification_type_id() {
        return notification_type_id;
    }

    public void setNotification_type_id(int notification_type_id) {
        this.notification_type_id = notification_type_id;
    }

}
