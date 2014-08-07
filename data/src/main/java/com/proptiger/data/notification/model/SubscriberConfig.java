package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subscriber_config")
public class SubscriberConfig {

    public enum ConfigName {
        MaxActiveNotificationTypeCount;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int        id;

    private Subscriber subscriber;

    @Column(name = "config_name")
    @Enumerated(EnumType.STRING)
    private ConfigName configName;

    @Column(name = "config_value")
    private String     configValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public ConfigName getConfigName() {
        return configName;
    }

    public void setConfigName(ConfigName configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

}
