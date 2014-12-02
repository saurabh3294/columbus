package com.proptiger.data.model.trend;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.proptiger.core.model.proptiger.UserSubscriptionMapping;

@Entity
@Table(name = "trend_reports")
public class TrendReportLog {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column(name = "usm_id")
    private Integer usmId;

    @Column(name = "download_date")
    private Date    downloadDate;

    @Column(name = "info")
    private String  info;

    @Column(name = "success")
    private Boolean success = false;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usm_id", referencedColumnName = "id", insertable=false, updatable=false)
    UserSubscriptionMapping usms;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsmId() {
        return usmId;
    }

    public void setUsmId(Integer usmId) {
        this.usmId = usmId;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
