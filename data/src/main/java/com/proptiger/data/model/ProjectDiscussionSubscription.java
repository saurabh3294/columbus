package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "project_discussion_subscription")
public class ProjectDiscussionSubscription extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               userId;

    @Column(name = "is_subscribed")
    private boolean           subscribed       = true;

    @Column(name = "unsubscribed_at")
    private Date              unsubscribedAt;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Date getUnsubscribedAt() {
        return unsubscribedAt;
    }

    public void setUnsubscribedAt(Date unsubscribedAt) {
        this.unsubscribedAt = unsubscribedAt;
    }
}