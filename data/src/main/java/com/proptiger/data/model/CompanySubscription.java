package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * 
 * @author azi
 * 
 */
@Entity(name = "company_subscriptions")
public class CompanySubscription extends BaseModel {
    private static final long    serialVersionUID = 1L;
    @Id
    private int                  id;

    @Column(name = "company_id")
    private int                  companyId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, targetEntity = Company.class)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company              company;

    @Column(name = "created_by")
    private int                  createdBy;

    @Column(name = "expiry_time")
    private Date                 expiryTime;

    @Column(name = "created_at")
    private Date                 createdAt;

    @Column(name = "updated_at")
    private Date                 updatedAt;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "subscriptionId")
    List<SubscriptionSection>    sections;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subscriptionId")
    List<SubscriptionPermission> permissions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<SubscriptionSection> getSections() {
        return sections;
    }

    public void setSections(List<SubscriptionSection> sections) {
        this.sections = sections;
    }

    public List<SubscriptionPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SubscriptionPermission> permissions) {
        this.permissions = permissions;
    }
}