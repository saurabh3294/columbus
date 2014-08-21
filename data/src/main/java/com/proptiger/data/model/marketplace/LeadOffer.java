/**
 * 
 */
package com.proptiger.data.model.marketplace;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.Listing;

/**
 * @author mandeep
 * 
 */
@Entity
@JsonInclude(Include.NON_EMPTY)
@Table(name = "marketplace.lead_offers")
public class LeadOffer extends BaseModel {

    private static final long serialVersionUID = -4428374943776702328L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               id;

    @Column(name = "lead_id")
    private int               leadId;

    @Column(name = "agent_id")
    private int               agentId;

    @Column(name = "status_id")
    private int               statusId;

    @Column(name = "cycle_id")
    private int               cycleId;

    @Column(name = "created_at")
    private Date              createdAt        = new Date();

    @Column(name = "updated_at")
    private Date              updatedAt        = new Date();

    @JoinColumn(insertable = false, updatable = false, name = "lead_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Lead              lead;
    
    @ManyToMany
    @JoinTable(name="marketplace.lead_offered_listings",
        joinColumns=
            @JoinColumn(name = "lead_offer_id", referencedColumnName = "id"),
        inverseJoinColumns=
            @JoinColumn(name="listing_id", referencedColumnName="id")
        )
    private List<Listing> listings;   

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
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

    public int getCycleId() {
        return cycleId;
    }

    public void setCycleId(int cycleId) {
        this.cycleId = cycleId;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }
    
}
