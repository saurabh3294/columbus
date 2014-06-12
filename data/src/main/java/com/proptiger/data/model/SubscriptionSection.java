package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

/**
 * 
 * @author azi
 * 
 */
@Entity(name = "subscription_sections")
public class SubscriptionSection extends BaseModel {
    private static final long                            serialVersionUID = 1L;

    @Id
    private int                                          id;

    @Column(name = "subscription_id")
    private int                                          subscriptionId;

    @Enumerated(EnumType.STRING)
    private com.proptiger.data.enums.SubscriptionSection section;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public com.proptiger.data.enums.SubscriptionSection getSection() {
        return section;
    }

    public void setSection(com.proptiger.data.enums.SubscriptionSection section) {
        this.section = section;
    }
}
