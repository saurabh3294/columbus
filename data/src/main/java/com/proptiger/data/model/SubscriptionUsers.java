package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @author azi
 * 
 */
@Entity(name = "user_subscription_mappings")
public class SubscriptionUsers extends BaseModel {
    private static final long   serialVersionUID = 1L;

    @Id
    private int                 id;

    @Column(name = "user_id")
    private int                 userId;

    @Column(name = "subscription_id")
    private int                 subscriptionId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subscription_id", insertable = false, updatable = false)
    private CompanySubscription subscription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public CompanySubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(CompanySubscription subscription) {
        this.subscription = subscription;
    }
}
