package com.proptiger.data.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.event.constants.MediumType;
import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "notification_medium")
public class NotificationMedium extends BaseModel {
    
    /**
     * 
     */
    private static final long          serialVersionUID = -465471732121440482L;

    @Id
    @Column(name = "id")
    private int                        id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private MediumType                 name;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date                       endTime;

    /**
     * Minimum time gap in seconds required between two Notifications for a
     * particular user in a particular medium
     */
    @Column(name = "frequency_cycle_in_seconds")
    private long                       frequencyCycleInSeconds;

    /**
     * Maximum number of messages that needs to be sent to a user in a day for a
     * particular medium
     */
    @Column(name = "number_of_messages_per_user")
    private int                        numberOfMessagesPerUser;

    @Transient
    @JsonIgnore
    private transient MediumTypeConfig mediumTypeConfig;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MediumType getName() {
        return name;
    }

    public void setName(MediumType name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getNumberOfMessagesPerUser() {
        return numberOfMessagesPerUser;
    }

    public void setNumberOfMessagesPerUser(int numberOfMessagesPerUser) {
        this.numberOfMessagesPerUser = numberOfMessagesPerUser;
    }

    public long getFrequencyCycleInSeconds() {
        return frequencyCycleInSeconds;
    }

    public void setFrequencyCycleInSeconds(long frequencyCycleInSeconds) {
        this.frequencyCycleInSeconds = frequencyCycleInSeconds;
    }

    public MediumTypeConfig getMediumTypeConfig() {
        return mediumTypeConfig;
    }

    public void setMediumTypeConfig(MediumTypeConfig mediumTypeConfig) {
        this.mediumTypeConfig = mediumTypeConfig;
    }
}
