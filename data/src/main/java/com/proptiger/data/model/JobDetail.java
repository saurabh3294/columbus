package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "JOBS_DETAILS")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class JobDetail extends BaseModel{

    private static final long serialVersionUID = 5607834101507449318L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "JOBS_CODE")
    private String jobCode;
    @Column(name = "EXP_REQ")
    private String expRequired;
    @Column(name = "JOBS_TITLE")
    private String jobTitle;
    @Column(name = "LOCATION")
    private String location;
    @Column(name = "JOBS_DESCRIPTION")
    private String jobDescription;
    @Column(name = "POSTED_DATE")
    private Date postedDate;
    @Column(name = "STATUS")
    private int status;
    @Column(name = "DELETED_FLAG")
    private int deletedFlag;
    @Column(name = "DEPARTMENT")
    private String department;
    @Column(name = "NOOFPOSITION")
    private String noOfPosition;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getJobCode() {
        return jobCode;
    }
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }
    public String getExpRequired() {
        return expRequired;
    }
    public void setExpRequired(String expRequired) {
        this.expRequired = expRequired;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getJobDescription() {
        return jobDescription;
    }
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    public Date getPostedDate() {
        return postedDate;
    }
    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getDeletedFlag() {
        return deletedFlag;
    }
    public void setDeletedFlag(int deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getNoOfPosition() {
        return noOfPosition;
    }
    public void setNoOfPosition(String noOfPosition) {
        this.noOfPosition = noOfPosition;
    }
    
    
    
}
