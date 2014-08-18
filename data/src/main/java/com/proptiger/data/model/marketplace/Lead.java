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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.user.User;

/**
 * @author Anubhav
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "marketplace.leads")
@JsonFilter("fieldFilter")
public class Lead extends BaseModel {

    /**
     * 
     */
    private static final long     serialVersionUID = -6647164101899851831L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                   id;

    @Column(name = "client_id")
    private int                   clientId;

    @Column(name = "city_id")
    private int                   cityId;

    @Column(name = "min_budget")
    private Integer               minBudget;

    @Column(name = "max_budget")
    private Integer               maxBudget;

    @Column(name = "min_size")
    private Integer               minSize;

    @Column(name = "max_size")
    private Integer               maxSize;

    @Column(name = "client_type")
    private String                clientType;

    @Column(name = "source_id")
    private int                   sourceId;

    @Column(name = "merged_lead_id")
    private Integer               mergedLeadId;

    @Column(name = "transaction_type")
    private String                transactionType;

    @Column(name = "next_action_time")
    private Date                  nextActionTime;

    @Column(name = "updated_at")
    private Date                  updatedAt        = new Date();

    @Column(name = "created_at")
    private Date                  createdAt        = new Date();

    @Column(name = "updated_by")
    private Integer               updatedBy;

    @OneToMany(mappedBy = "leadId")
    private List<LeadOffer>       leadOffers;

    @JoinColumn(insertable = false, updatable = false, name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User                  client;

    @OneToMany(mappedBy = "leadId")
    private List<LeadRequirement> requirements;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getMergedLeadId() {
        return mergedLeadId;
    }

    public void setMergedLeadId(Integer mergedLeadId) {
        this.mergedLeadId = mergedLeadId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public Integer getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(Integer minBudget) {
        this.minBudget = minBudget;
    }

    public Integer getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(Integer maxBudget) {
        this.maxBudget = maxBudget;
    }

    public Integer getMinSize() {
        return minSize;
    }

    public void setMinSize(Integer minSize) {
        this.minSize = minSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Date getNextActionTime() {
        return nextActionTime;
    }

    public void setNextActionTime(Date nextActionTime) {
        this.nextActionTime = nextActionTime;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @PreUpdate
    public void setUpdatedAtBeforeDBQuery() {
        this.updatedAt = new Date();
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = new Date();
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<LeadOffer> getLeadOffers() {
        return leadOffers;
    }

    public void setLeadOffers(List<LeadOffer> leadOffers) {
        this.leadOffers = leadOffers;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<LeadRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<LeadRequirement> requirements) {
        this.requirements = requirements;
    }
}
