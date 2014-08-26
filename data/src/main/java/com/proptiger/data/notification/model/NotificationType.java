package com.proptiger.data.notification.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.proptiger.data.event.model.EventType;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;

@Entity
@Table(name = "notification_type")
public class NotificationType extends BaseModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1899826990526820074L;

    public enum NotificationOperation {
        Merge, Suppress
    }

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int                              id;

    @Column(name = "name")
    private String                           name;

    /**
     * Minimum fixed delay required after notification generation before sending
     * it
     */
    @Column(name = "fixed_delay_in_seconds")
    private Long                             fixedDelay;

    /**
     * This flag denotes if a notification type can be rescheduled or it will be
     * suppressed if it cannot be sent in the current time frame
     */
    @Column(name = "can_reschedule")
    private Boolean                          canReschedule;

    @Column(name = "no_of_reschedule")
    private Integer                          numberOfReschedule;

    /**
     * Minimum time gap in seconds required between two Notifications of same
     * Notification Type with same primary key for a particular user in a
     * particular medium
     */
    @Column(name = "frequency_cycle_in_seconds")
    private Long                             frequencyCycleInSeconds;

    /**
     * Priority of a Notification type over another type
     */
    @Column(name = "priority")
    private Integer                          priority;

    @Column(name = "overwrite_config_name")
    private String                           overwriteConfigName;

    @Transient
    private List<EventType>                  eventTypeList;

    @Transient
    @JsonIgnore
    @Expose(serialize = false, deserialize = false)
    private transient NotificationTypeConfig notificationTypeConfig;

    @Column(name = "intra_primary_key_operation")
    @Enumerated(EnumType.STRING)
    private NotificationOperation            intraPrimaryKeyOperation;

    @Column(name = "intra_non_primary_key_operation")
    @Enumerated(EnumType.STRING)
    private NotificationOperation            intraNonPrimaryKeyOperation;

    @Column(name = "inter_primary_key_merge_id")
    private Integer                          interPrimaryKeyMergeId;

    @Column(name = "inter_primary_key_suppress_id")
    private Integer                          interPrimaryKeySuppressId;

    @Column(name = "inter_non_primary_key_merge_id")
    private Integer                          interNonPrimaryKeyMergeId;

    @Column(name = "inter_non_primary_key_suppress_id")
    private Integer                          interNonPrimaryKeySuppressId;

    /**
     * Strategy for getting the list of users for a particular notification type
     */
    @Column(name = "user_strategy")
    @Enumerated(EnumType.STRING)
    private NotificationTypeUserStrategy     userStrategy;

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

    public Long getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(Long fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public Boolean getCanReschedule() {
        return canReschedule;
    }

    public void setCanReschedule(Boolean canReschedule) {
        this.canReschedule = canReschedule;
    }

    public Integer getNumberOfReschedule() {
        return numberOfReschedule;
    }

    public void setNumberOfReschedule(Integer numberOfReschedule) {
        this.numberOfReschedule = numberOfReschedule;
    }

    public Long getFrequencyCycleInSeconds() {
        return frequencyCycleInSeconds;
    }

    public void setFrequencyCycleInSeconds(Long frequencyCycleInSeconds) {
        this.frequencyCycleInSeconds = frequencyCycleInSeconds;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public NotificationOperation getIntraPrimaryKeyOperation() {
        return intraPrimaryKeyOperation;
    }

    public void setIntraPrimaryKeyOperation(NotificationOperation intraPrimaryKeyOperation) {
        this.intraPrimaryKeyOperation = intraPrimaryKeyOperation;
    }

    public NotificationOperation getIntraNonPrimaryKeyOperation() {
        return intraNonPrimaryKeyOperation;
    }

    public void setIntraNonPrimaryKeyOperation(NotificationOperation intraNonPrimaryKeyOperation) {
        this.intraNonPrimaryKeyOperation = intraNonPrimaryKeyOperation;
    }

    public Integer getInterPrimaryKeyMergeId() {
        return interPrimaryKeyMergeId;
    }

    public void setInterPrimaryKeyMergeId(Integer interPrimaryKeyMergeId) {
        this.interPrimaryKeyMergeId = interPrimaryKeyMergeId;
    }

    public Integer getInterPrimaryKeySuppressId() {
        return interPrimaryKeySuppressId;
    }

    public void setInterPrimaryKeySuppressId(Integer interPrimaryKeySuppressId) {
        this.interPrimaryKeySuppressId = interPrimaryKeySuppressId;
    }

    public Integer getInterNonPrimaryKeyMergeId() {
        return interNonPrimaryKeyMergeId;
    }

    public void setInterNonPrimaryKeyMergeId(Integer interNonPrimaryKeyMergeId) {
        this.interNonPrimaryKeyMergeId = interNonPrimaryKeyMergeId;
    }

    public Integer getInterNonPrimaryKeySuppressId() {
        return interNonPrimaryKeySuppressId;
    }

    public void setInterNonPrimaryKeySuppressId(Integer interNonPrimaryKeySuppressId) {
        this.interNonPrimaryKeySuppressId = interNonPrimaryKeySuppressId;
    }

    public NotificationTypeUserStrategy getUserStrategy() {
        return userStrategy;
    }

    public void setUserStrategy(NotificationTypeUserStrategy userStrategy) {
        this.userStrategy = userStrategy;
    }

}
