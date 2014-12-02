package com.proptiger.data.model.trend;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trend_reports")
public class TrendReportLog {

    @Id
    private Integer id;

    @Column(name = "usm_id")
    private Integer usmId;

    @Column(name = "download_date")
    private Date    downloadDate;

    @Column(name = "info")
    private String  info;

    @Column(name = "success")
    private Boolean success;
    
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
