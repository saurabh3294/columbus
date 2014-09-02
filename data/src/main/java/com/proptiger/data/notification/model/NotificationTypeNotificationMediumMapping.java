package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "notification_type_notification_medium_mapping")
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

    @OneToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationType   notificationType;
    
    @Column(name = "send_template")
    private String             sendTemplate;

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

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getSendTemplate() {
        return sendTemplate;
    }

    public void setSendTemplate(String sendTemplate) {
        this.sendTemplate = sendTemplate;
    }
}
