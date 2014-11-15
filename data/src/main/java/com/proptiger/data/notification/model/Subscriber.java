package com.proptiger.data.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "notification.subscriber")
public class Subscriber extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7389423816245412201L;

    public enum SubscriberName {
        Notification, Seo;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue
    private int            id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private SubscriberName subscriberName;

    @Column(name = "last_event_date")
    private Date           lastEventDate;
    
    @Column(name = "last_event_generated_id")
    private int lastEventGeneratedId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubscriberName getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(SubscriberName subscriberName) {
        this.subscriberName = subscriberName;
    }

    public Date getLastEventDate() {
        return lastEventDate;
    }

    public void setLastEventDate(Date lastEventDate) {
        this.lastEventDate = lastEventDate;
    }

    public int getLastEventGeneratedId() {
        return lastEventGeneratedId;
    }

    public void setLastEventGeneratedId(int lastEventGeneratedId) {
        this.lastEventGeneratedId = lastEventGeneratedId;
    }

}
