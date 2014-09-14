package com.proptiger.data.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.master_lead_tasks")
public class MasterLeadTask extends BaseModel {
    private static final long    serialVersionUID = 1L;

    @Id
    private int                  id;

    private String               name;

    @Column(name = "is_optional")
    private boolean              optional;

    @Column(name = "min_listing_count")
    private int                  minListingCount;

    @Column(name = "max_listing_count")
    private int                  maxListingCount;

    @Column(name = "execution_order")
    private int                  priority;

    @OneToMany(mappedBy = "masterTaskId")
    private List<LeadTaskStatus> leadTaskStatuses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getMinListingCount() {
        return minListingCount;
    }

    public void setMinListingCount(int minListingCount) {
        this.minListingCount = minListingCount;
    }

    public int getMaxListingCount() {
        return maxListingCount;
    }

    public void setMaxListingCount(int maxListingCount) {
        this.maxListingCount = maxListingCount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<LeadTaskStatus> getLeadTaskStatuses() {
        return leadTaskStatuses;
    }

    public void setLeadTaskStatuses(List<LeadTaskStatus> leadTaskStatuses) {
        this.leadTaskStatuses = leadTaskStatuses;
    }
}