package com.proptiger.data.notification.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.event.model.EventType;

@Entity
@Table(name = "notification_type")
public class NotificationType {

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int                              id;

    @Column(name = "name")
    private String                           name;

    @Column(name = "overwrite_config_name")
    private String                           overwriteConfigName;

    private List<EventType>                  eventTypeList;

    @Transient
    @JsonIgnore
    private transient NotificationTypeConfig notificationTypeConfig;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverwriteConfigName() {
        return overwriteConfigName;
    }

    public void setOverwriteConfigName(String overwriteConfigName) {
        this.overwriteConfigName = overwriteConfigName;
    }

    public List<EventType> getEventTypeList() {
        return eventTypeList;
    }

    public void setEventTypeList(List<EventType> eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    public NotificationTypeConfig getNotificationTypeConfig() {
        return notificationTypeConfig;
    }

    public void setNotificationTypeConfig(NotificationTypeConfig notificationTypeConfig) {
        this.notificationTypeConfig = notificationTypeConfig;
    }

}
