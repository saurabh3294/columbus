package com.proptiger.data.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;

@Entity
@Table(name = "notification_message")
public class NotificationMessage extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 4800603265035626921L;
    
    @Id
    @Column(name = "id")
    private int id;
    
    @OneToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationTypeGenerated notificationTypeGenerated;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private ForumUser forumUser;
    
    @Transient
    private NotificationMessagePayload notificationMessagePayload;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
}
