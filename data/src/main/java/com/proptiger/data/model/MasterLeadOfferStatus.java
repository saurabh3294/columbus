package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.master_lead_statuses")
public class MasterLeadOfferStatus extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Column(name = "status")
    private String            status;

    private int               level;

    @Column(name = "claimed_flag")
    private boolean           claimedFlag;

    @Column(name = "open_flag")
    private boolean           openFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isClaimed() {
        return claimedFlag;
    }

    public void setClaimed(boolean claimedFlag) {
        this.claimedFlag = claimedFlag;
    }

    public boolean isOpen() {
        return openFlag;
    }

    public void setOpen(boolean openFlag) {
        this.openFlag = openFlag;
    }

    public int getLevel() {
        return level;
    }
}