/**
 * 
 */
package com.proptiger.data.model.marketplace;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

/**
 * @author mandeep
 *
 */
@Entity
@Table(name="marketplace.lead_offers")
public class LeadOffer extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -4428374943776702328L;

    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "lead_id")
    private int leadId;
    
    @Column(name = "agent_id")
    private int agentId;
    
    @Column(name = "status_id")
    private int statusId;
    
    @Column(name = "cycle_id")
    private int cycleId;
    
    @Column(name = "created_at")
    private Date createdAt = new Date();
    
    @Column(name = "updated_at")
    private Date updatedAt = new Date();

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
}
