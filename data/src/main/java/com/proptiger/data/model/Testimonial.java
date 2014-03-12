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
@Table(name = "TESTIMONIALS")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Testimonial extends BaseModel{
    private static final long serialVersionUID = -1436642126048818565L;
    @Id
    @Column(name = "CLIENT_ID")
    private Integer clientId;
    @Column(name = "CLIENT_NAME")
    private String clientName;
    @Column(name = "COM_NAME")
    private String companyName;
    @Column(name = "CLIENT_DESIGNATION")
    private String clientDesignation;
    @Column(name = "CLIENT_IMAGE")
    private String clientImage;
    @Column(name = "CLIENT_PHONE")
    private String clientPhone;
    @Column(name = "CLIENT_COMMENT")
    private String clientComment;
    @Column(name = "CLIENT_EMAIL")
    private String clientEmail;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "MODIFIED_DATE")
    private Date modifiedDate;
    
    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getClientDesignation() {
        return clientDesignation;
    }
    public void setClientDesignation(String clientDesignation) {
        this.clientDesignation = clientDesignation;
    }
    public String getClientImage() {
        return clientImage;
    }
    public void setClientImage(String clientImage) {
        this.clientImage = clientImage;
    }
    public String getClientPhone() {
        return clientPhone;
    }
    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }
    public String getClientComment() {
        return clientComment;
    }
    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }
    public String getClientEmail() {
        return clientEmail;
    }
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public Date getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
}
