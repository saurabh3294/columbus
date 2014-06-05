package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.proptiger.data.enums.SubscriptionColumnGroup;

/**
 * 
 * @author azi
 * 
 */
@Entity(name = "company_permission_columns")
public class SubscriptionColumn extends BaseModel {
    private static final long       serialVersionUID = 1L;

    @Column(name = "subscription_id")
    private int                     subscriptionId;

    @Column(name = "column_group")
    private SubscriptionColumnGroup columnGroup;

    @Column(name = "created_by")
    private int                     createdBy;

    @Column(name = "created_at")
    private Date                    createdAt;
}
