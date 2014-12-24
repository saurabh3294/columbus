package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.event.EventType;

@Entity
@Table(name = "notification.event_type_to_notification_type_mapping")
public class EventTypeToNotificationTypeMapping extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -4664822896651901749L;

    @Column(name = "id")
    @Id
    private int              id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_type_id")
    private EventType        eventType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

}
