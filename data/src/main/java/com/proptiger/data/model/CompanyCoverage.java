package com.proptiger.data.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "cms.company_coverage")
public class CompanyCoverage extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Column(name = "company_id")
    private int               companyId;

    @Column(name = "locality_id")
    private int               localityId;

    @OneToOne
    @JoinColumn(name = "locality_id" , referencedColumnName = "LOCALITY_ID", insertable = false, updatable = false)
    private Locality locality;
    
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

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    
}