/**
 * 
 */
package com.proptiger.data.model.marketplace;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.user.User;

/**
 * @author mandeep
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity(name = "leads")
@Table(name = "marketplace.leads")
@JsonFilter("fieldFilter")
public class Lead extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -6647164101899851831L;

    @Id
    @Column(name = "id")
    private int               id;

    @Column(name = "client_id")
    private int               clientId;

    @Column(name = "city_id")
    private int               cityId;

    @Column(name = "min_budget")
    private Integer           minBudget;

    @Column(name = "max_budget")
    private Integer           maxBudget;

    @Column(name = "min_size")
    private Integer           minSize;

    @Column(name = "max_size")
    private Integer           maxSize;

    @Column(name = "client_type")
    @Enumerated
    private ClientType        clientType;

    @Column(name = "transaction_type")
    @Enumerated
    private TransactionType   transactionType;

    @Column(name = "score")
    private int               score;

    @Column(name = "irritated_client")
    private boolean           irritatedClient;

    @Column(name = "updated_at")
    private Date              updatedAt        = new Date();

    @Column(name = "created_at")
    private Date              createdAt        = new Date();

    @Column(name = "updated_by")
    private Integer           updatedBy;

    @OneToMany(mappedBy = "leadId", fetch = FetchType.EAGER)
    private List<LeadOffer>   leadOffers;
    
    @Transient
    private User client;

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

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isIrritatedClient() {
        return irritatedClient;
    }

    public void setIrritatedClient(boolean irritatedClient) {
        this.irritatedClient = irritatedClient;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
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
}
