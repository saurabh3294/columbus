package com.proptiger.data.notification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "notification.subscriber_config")
public class SubscriberConfig extends BaseModel {
  
    /**
     * 
     */
    private static final long serialVersionUID = 66872383451935024L;

    public enum ConfigName {
        MaxActiveNotificationTypeCount, MaxActiveNotificationMessageCount, MaxVerifedEventCount;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int        id;

    @OneToOne
    @JoinColumn(name = "subscriber_id")
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
