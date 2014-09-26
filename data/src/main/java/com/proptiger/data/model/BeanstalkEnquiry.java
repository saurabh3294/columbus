package com.proptiger.data.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeanstalkEnquiry {
    
    private String status;
    
    @JsonProperty("project_name")
    private String projectName;
    
    private String locality;
    
    @JsonProperty("city_name")
    private String cityName;
   
    @JsonProperty("lead_type")
    private String leadType;
    
    
    private String leadTag;
    
    @JsonProperty("lead_owner")
    private String leadOwner;
    
    @JsonProperty("lead_owner_id")
    private String leadOwnerId;
    
    @JsonProperty("ref_by")
    private String referrer;
    
    @JsonProperty("ref_lead_id")
    private String referrerLeadId;
    
    @JsonProperty("sub_broker_detail")
    private String subbrokerDetails;
    
    @JsonProperty("dead_reason")
    private String deadReason;
    
    @JsonProperty("follow_date")
    private String followUpDate;
 
    // Enquiry Class fields
    private long id;
    
    private String name;
    
    private String email;
    
    private String phone;
    
    private String country;
    
    @JsonProperty("project_id")
    private Integer projectId;
    
    @JsonProperty("locality_id")
    private Integer localityId;

    private String query;
    
    private String description;
    
    private String source;
    
    @JsonProperty("user_medium")
    private String userMedium;
    
    @JsonProperty("user_network")
    private String userNetwork;
    
    @JsonProperty("enq_time")
    private Date enquiryTime;
    
    @JsonProperty("application_type")
    private String applicationType;

    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLeadType() {
        return leadType;
    }

    public void setLeadType(String leadType) {
        this.leadType = leadType;
    }

    public String getLeadTag() {
        return leadTag;
    }

    public void setLeadTag(String leadTag) {
        this.leadTag = leadTag;
    }

    public String getLeadOwner() {
        return leadOwner;
    }

    public void setLeadOwner(String leadOwner) {
        this.leadOwner = leadOwner;
    }

    public String getLeadOwnerId() {
        return leadOwnerId;
    }

    public void setLeadOwnerId(String leadOwnerId) {
        this.leadOwnerId = leadOwnerId;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getReferrerLeadId() {
        return referrerLeadId;
    }

    public void setReferrerLeadId(String referrerLeadId) {
        this.referrerLeadId = referrerLeadId;
    }

    public String getSubbrokerDetails() {
        return subbrokerDetails;
    }

    public void setSubbrokerDetails(String subbrokerDetails) {
        this.subbrokerDetails = subbrokerDetails;
    }

    public String getDeadReason() {
        return deadReason;
    }

    public void setDeadReason(String deadReason) {
        this.deadReason = deadReason;
    }

    public String getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(String followUpDate) {
        this.followUpDate = followUpDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserMedium() {
        return userMedium;
    }

    public void setUserMedium(String userMedium) {
        this.userMedium = userMedium;
    }

    public String getUserNetwork() {
        return userNetwork;
    }

    public void setUserNetwork(String userNetwork) {
        this.userNetwork = userNetwork;
    }

    public Date getEnquiryTime() {
        return enquiryTime;
    }

    public void setEnquiryTime(Date enquiryTime) {
        this.enquiryTime = enquiryTime;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }
}