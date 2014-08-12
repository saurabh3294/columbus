package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "notification_Type_notification_medium_mapping")
public class NotificationTypeNotificationMediumMapping extends BaseModel {

    /**
     * 
     */
    private static final long  serialVersionUID = 6735194738652570322L;

    @Id
    @Column(name = "id")
    private int                id;

    @OneToOne
    @JoinColumn(name = "notification_medium_id")
    private NotificationMedium notificationMedium;

    @Column(name = "notification_type_id")
    private int                notification_type_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NotificationMedium getNotificationMedium() {
        return notificationMedium;
    }

    public void setNotificationMedium(NotificationMedium notificationMedium) {
        this.notificationMedium = notificationMedium;
    }

    public int getNotification_type_id() {
        return notification_type_id;
    }

    public void setNotification_type_id(int notification_type_id) {
        this.notification_type_id = notification_type_id;
    }
}
