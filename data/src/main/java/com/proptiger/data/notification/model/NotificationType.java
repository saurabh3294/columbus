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
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;

@Entity
@Table(name = "notification_type")
public class NotificationType extends BaseModel {
    public enum NotificationOperation {
        Merge, Suppress
    }

    /**
     * 
     */
    private static final long                serialVersionUID = 549033224673052141L;

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int                              id;

    @Column(name = "name")
    private String                           name;

    @Column(name = "overwrite_config_name")
    private String                           overwriteConfigName;

    @Transient
    @JsonIgnore
    private transient NotificationTypeConfig notificationTypeConfig;

    @Transient
    private List<NotificationMedium>         notificationMediumList;

    @Transient
    private List<ForumUser>                  forumUserList;

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

    public NotificationTypeConfig getNotificationTypeConfig() {
        return notificationTypeConfig;
    }

    public void setNotificationTypeConfig(NotificationTypeConfig notificationTypeConfig) {
        this.notificationTypeConfig = notificationTypeConfig;
    }

    public List<NotificationMedium> getNotificationMediumList() {
        return notificationMediumList;
    }

    public void setNotificationMediumList(List<NotificationMedium> notificationMediumList) {
        this.notificationMediumList = notificationMediumList;
    }

    public List<ForumUser> getForumUserList() {
        return forumUserList;
    }

    public void setForumUserList(List<ForumUser> forumUserList) {
        this.forumUserList = forumUserList;
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

}
