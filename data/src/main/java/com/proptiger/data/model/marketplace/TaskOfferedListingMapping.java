package com.proptiger.data.model.marketplace;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "marketplace.lead_task_listing_mappings")
public class TaskOfferedListingMapping extends BaseModel {
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer            id;

    @Column(name = "task_id")
    private int                taskId;

    @Column(name = "lead_offered_listing_id")
    private int                listingOfferId;

    @Column(name = "created_at")
    private Date               createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_offered_listing_id", insertable = false, updatable = false)
    private LeadOfferedListing offeredListing;

    @PrePersist
    private void prePersist() {
        createdAt = new Date();
    }

    public TaskOfferedListingMapping(int taskId, int listingOfferId) {
        this.taskId = taskId;
        this.listingOfferId = listingOfferId;
    }

    public TaskOfferedListingMapping() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getListingOfferId() {
        return listingOfferId;
    }

    public void setListingOfferId(int listingOfferId) {
        this.listingOfferId = listingOfferId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public LeadOfferedListing getOfferedListing() {
        return offeredListing;
    }

    public void setOfferedListing(LeadOfferedListing offeredListing) {
        this.offeredListing = offeredListing;
    }
}