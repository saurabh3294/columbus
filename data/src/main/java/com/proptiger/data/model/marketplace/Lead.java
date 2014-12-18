/**
 * 
 */
package com.proptiger.data.model.marketplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.user.User;

/**
 * @author Anubhav
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "marketplace.leads")
@JsonFilter("fieldFilter")
public class Lead extends BaseModel {

    private static final long     serialVersionUID      = -6647164101899851831L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
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
    private String                clientType            = "Buyer";

    @Column(name = "source_id")
    private int                   sourceId;

    @Column(name = "merged_lead_id")
    private Integer               mergedLeadId;

    @Column(name = "transaction_type")
    private String                transactionType       = "Resale";

    @Column(name = "next_action_time")
    private Date                  nextActionTime;

    @Column(name = "notes")
    private String                specialRequirements;

    @Transient
    private String                derivedBedroomsString = "";

    @Transient
    private int                   countAgentsClaimed    = 99999999;

    @Column(name = "created_at")
    private Date                  createdAt             = new Date();

    @Column(name = "updated_at")
    private Date                  updatedAt             = new Date();

    @Column(name = "updated_by")
    private Integer               updatedBy;

    @OneToMany(mappedBy = "leadId")
    private List<LeadOffer>       leadOffers;

    @Transient
    private User                  client;

    @OneToMany(mappedBy = "leadId")
    private List<LeadRequirement> requirements;

    @Column(name = "flexible_budget_flag")
    private boolean               flexibleBudget    = false;

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
        populateDerivedBedroomsString();
    }

    private void populateDerivedBedroomsString() {
        Set<Integer> bedrooms = new HashSet<>();

        if (requirements != null) {
            for (LeadRequirement leadRequirement : requirements) {
                if (leadRequirement.getBedroom() != null) {
                    bedrooms.add(leadRequirement.getBedroom());
                }
            }

            if (!bedrooms.isEmpty()) {
                Collections.sort(new ArrayList<>(bedrooms));
                derivedBedroomsString = StringUtils.join(bedrooms, ',') + " BHK";
            }
        }
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getDerivedBedroomsString() {
        return derivedBedroomsString;
    }

    public void setDerivedBedroomsString(String derivedBedroomsString) {
        this.derivedBedroomsString = derivedBedroomsString;
    }

    public boolean isFlexibleBudget() {
        return flexibleBudget;
    }

    public void setFlexibleBudget(boolean flexibleBudgetFlag) {
        this.flexibleBudget = flexibleBudgetFlag;
    }
    
}
