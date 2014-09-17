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
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.MasterLeadOfferStatus;
import com.proptiger.data.model.user.User;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;

/**
 * @author mandeep
 * 
 */
@Entity
@JsonInclude(Include.NON_EMPTY)
@Table(name = "marketplace.lead_offers")
public class LeadOffer extends BaseModel {
    private static final long serialVersionUID = -4428374943776702328L;

    public static class CountListingObject {
        private Integer leadOfferId;
        private long    countListings;

        public Integer getLeadOfferId() {
            return leadOfferId;
        }

        public void setLeadOfferId(Integer leadOfferId) {
            this.leadOfferId = leadOfferId;
        }

        public long getCountListings() {
            return countListings;
        }

        public void setCountListings(long countListings) {
            this.countListings = countListings;
        }

        public CountListingObject(Integer leadOfferId, long countListings) {
            super();
            this.leadOfferId = leadOfferId;
            this.countListings = countListings;
        }
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                      id;

    @Column(name = "lead_id")
    @JsonIgnore
    private int                      leadId;

    @Column(name = "agent_id")
    private int                      agentId;

    @Column(name = "status_id")
    private Integer                  statusId;

    @Column(name = "cycle_id")
    @JsonIgnore
    private int                      cycleId;

    @Column(name = "previous_task_id")
    private Integer                  lastTaskId;

    @Column(name = "next_task_id")
    private Integer                  nextTaskId;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LeadTask                 lastTask;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_task_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LeadTask                 nextTask;

    @OneToMany(mappedBy = "leadOfferId")
    private List<LeadTask>           tasks;

    @Transient
    private int                      countMatchingListings = 99999999;

    @Transient
    private int                      countOfferedListings  = 99999999;

    @Column(name = "created_at")
    private Date                     createdAt             = new Date();

    @Column(name = "updated_at")
    private Date                     updatedAt             = new Date();

    @JoinColumn(insertable = false, updatable = false, name = "lead_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Lead                     lead;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_offer_id", referencedColumnName = "id")
    private List<LeadOfferedListing> offeredListings;

    @Transient
    private LeadOfferedListing       latestOfferedListing;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "agent_id", insertable = false, updatable = false)
    private List<Listing>            matchingListings;

    @OneToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", insertable = false, updatable = false)
    private MasterLeadOfferStatus    masterLeadOfferStatus;

    @Transient
    private User                     agent;

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    @Transient
    private Date expireTimestamp;

    @PostLoad
    public void evaluateExpiryTimestamp() {
        expireTimestamp = DateUtil
                .getWorkingTimeAddedIntoDate(
                        createdAt,
                        PropertyReader.getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_BIDDING_CYCLE_DURATION) + PropertyReader
                                .getRequiredPropertyAsInt(PropertyKeys.MARKETPLACE_POST_BIDDING_OFFER_DURATION));
    }

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

    @PreUpdate
    public void populateUpdatedAt() {
        updatedAt = new Date();
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

    public LeadTask getLastTask() {
        return lastTask;
    }

    public void setLastTask(LeadTask lastTask) {
        this.lastTask = lastTask;
    }

    public LeadTask getNextTask() {
        return nextTask;
    }

    public void setNextTask(LeadTask nextTask) {
        this.nextTask = nextTask;
    }

    public List<Listing> getMatchingListings() {
        return matchingListings;
    }

    public void setMatchingListings(List<Listing> matchingListings) {
        this.matchingListings = matchingListings;
    }

    public List<LeadTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<LeadTask> tasks) {
        this.tasks = tasks;
    }

    public int getCountMatchingListings() {
        return countMatchingListings;
    }

    public void setCountMatchingListings(int countMatchingListings) {
        this.countMatchingListings = countMatchingListings;
    }

    public int getCountOfferedListings() {
        return countOfferedListings;
    }

    public void setCountOfferedListings(int countOfferedListings) {
        this.countOfferedListings = countOfferedListings;
    }

    public List<LeadOfferedListing> getOfferedListings() {
        return offeredListings;
    }

    public void setOfferedListings(List<LeadOfferedListing> offeredListings) {
        this.offeredListings = offeredListings;
    }

    public LeadOfferedListing getLatestOfferedListing() {
        return latestOfferedListing;
    }

    public void setLatestOfferedListing(LeadOfferedListing latestOfferedListing) {
        this.latestOfferedListing = latestOfferedListing;
    }

    public MasterLeadOfferStatus getMasterLeadOfferStatus() {
        return masterLeadOfferStatus;
    }

    public void setMasterLeadOfferStatus(MasterLeadOfferStatus masterLeadOfferStatus) {
        this.masterLeadOfferStatus = masterLeadOfferStatus;
    }

    public Integer getLastTaskId() {
        return lastTaskId;
    }

    public void setLastTaskId(Integer lastTaskId) {
        this.lastTaskId = lastTaskId;
    }

    public Integer getNextTaskId() {
        return nextTaskId;
    }

    public void setNextTaskId(Integer nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    public Date getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(Date expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

}
