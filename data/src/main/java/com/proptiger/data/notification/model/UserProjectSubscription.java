package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "notification.user_project_subscription")
public class UserProjectSubscription extends BaseModel {

    private static final long                serialVersionUID = 1359897751007848363L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int                              id;

    @ManyToOne
    @JoinColumn(name = "notification_type_subscription_id")
    private UserNotificationTypeSubscription notificationTypeSubscription;

    @Column(name = "project_id")
    private int                              projectId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserNotificationTypeSubscription getNotificationTypeSubscription() {
        return notificationTypeSubscription;
    }

    public void setNotificationTypeSubscription(UserNotificationTypeSubscription notificationTypeSubscription) {
        this.notificationTypeSubscription = notificationTypeSubscription;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

}
