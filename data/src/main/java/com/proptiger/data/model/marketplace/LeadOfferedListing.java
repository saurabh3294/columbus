/**
 * 
 */
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
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.cms.Listing;

/**
 * @author Anubhav
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "marketplace.lead_offered_listings")
@JsonFilter("fieldFilter")
public class LeadOfferedListing extends BaseModel {
    private static final long serialVersionUID = -6647164101899851831L;

    public LeadOfferedListing() {
    }

    public LeadOfferedListing(int leadOfferId, int listingId) {
        this.leadOfferId = leadOfferId;
        this.listingId = listingId;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int     id;

    @Column(name = "lead_offer_id")
    private int     leadOfferId;

    @Column(name = "listing_id")
    private int     listingId;

    @Column(name = "created_at")
    private Date    createdAt;

    @Column(name = "updated_at")
    private Date    updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", insertable = false, updatable = false)
    private Listing listing;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadOfferId() {
        return leadOfferId;
    }

    public void setLeadOfferId(int leadOfferId) {
        this.leadOfferId = leadOfferId;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void setUpdatedAtBeforeDBQuery() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = new Date();
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }
}
