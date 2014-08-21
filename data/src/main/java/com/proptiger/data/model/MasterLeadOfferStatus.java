package com.proptiger.data.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    private String            status;

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
}