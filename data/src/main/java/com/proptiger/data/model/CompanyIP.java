package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.company_ips")
public class CompanyIP extends BaseModel{
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;
    
    @Column(name = "company_id")
    private Integer companyId;
    
    @Column(name = "ip")
    private String  ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
