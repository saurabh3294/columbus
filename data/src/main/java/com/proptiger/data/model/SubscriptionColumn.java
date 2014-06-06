package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.proptiger.data.enums.SubscriptionColumnGroup;

/**
 * 
 * @author azi
 * 
 */
@Entity(name = "subscription_columns")
public class SubscriptionColumn extends BaseModel {
    private static final long       serialVersionUID = 1L;

    @Id
    private int                     id;

    @Column(name = "subscription_id")
    private int                     subscriptionId;

    @Column(name = "column_group")
    @Enumerated(EnumType.STRING)
    private SubscriptionColumnGroup columnGroup;

    @Column(name = "created_by")
    private int                     createdBy;

    @Column(name = "created_at")
    private Date                    createdAt;

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

    public SubscriptionColumnGroup getColumnGroup() {
        return columnGroup;
    }

    public void setColumnGroup(SubscriptionColumnGroup columnGroup) {
        this.columnGroup = columnGroup;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
