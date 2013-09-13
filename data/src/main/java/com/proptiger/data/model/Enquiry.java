/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author mukand
 */


@Entity
@Table(name = "ENQUIRY")
public class Enquiry {
    @Column(name = "ID")
    @Id
    private long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "COUNTRY_OF_RESIDENCE")
    private String countryOfResidence;
    @Column(name = "QUERY")
    private String query;
    @Column(name = "PROJECT_ID", nullable = true)
    private Long projectId;
    @Column(name = "PROJECT_NAME")
    private String projectName;
    @Column(name = "CITY_ID")
    private Integer cityId;
    @Column(name = "CITY_NAME")
    private String cityName;
    @Column(name = "LOCALITY_ID", insertable = false, updatable = false, nullable = true)
    private int localityId;
    @Column(name = "IP")
    private String ip;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "SOURCE")
    private String source;
    @Column(name = "FORM_NAME")
    private String formName;
    @Column(name = "PAGE_NAME")
    private String pageName;
    @Column(name = "PAGE_URL")
    private String pageUrl;
    @Column(name = "REF_URL")
    private String refUrl;
    @Column(name = "AD_GRP")
    private String adGrp;
    @Column(name = "KEYWORDS")
    private String keywords;
    @Column(name = "CAMPAIGN")
    private String campaign;
    @Column(name = "PPC")
    private String ppc;
    @Column(name = "USER")
    private String user;
    @Column(name = "HTTP_REFERER")
    private String httpReferer;
    @Column(name = "USER_MEDIUM")
    private String userMedium;
    @Column(name = "GA_SOURCE")
    private String gaSource;
    @Column(name = "GA_MEDIUM")
    private String gaMedium;
    @Column(name = "GA_NETWORK")
    private String gaNetwork;
    @Column(name = "GA_KEYWORDS")
    private String gaKeywords;
    @Column(name = "GA_CAMPAIGN")
    private String gaCampaign;
    @Column(name = "GA_USER_ID")
    private String gaUserId;
    @Column(name = "GA_TIMESPENT")
    private String gaTimespent;
    
    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", referencedColumnName = "LOCALITY_ID")
    private Locality locality;

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }
    
    private enum buyPeriod  {
        NOW(0), LAST_90_DAYS(1), LAST_180_DAYS(2);
        int x;
        buyPeriod(int x) {
            this.x = x;
        }
    }
    private enum processingStatus {
        UNSUCCESSFUL,PROCESSING,PROCESSED;
    }
    private enum gaPpc {
        ZERO, ONE;
    }
    private enum registeredUser {
        YES, NO, NULL
    }
    private enum leadSaleType {
        PRIMARY, RESALE;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public String getAdGrp() {
        return adGrp;
    }

    public void setAdGrp(String adGrp) {
        this.adGrp = adGrp;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getPpc() {
        return ppc;
    }

    public void setPpc(String ppc) {
        this.ppc = ppc;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public String getUserMedium() {
        return userMedium;
    }

    public void setUserMedium(String userMedium) {
        this.userMedium = userMedium;
    }

    public String getGaSource() {
        return gaSource;
    }

    public void setGaSource(String gaSource) {
        this.gaSource = gaSource;
    }

    public String getGaMedium() {
        return gaMedium;
    }

    public void setGaMedium(String gaMedium) {
        this.gaMedium = gaMedium;
    }

    public String getGaNetwork() {
        return gaNetwork;
    }

    public void setGaNetwork(String gaNetwork) {
        this.gaNetwork = gaNetwork;
    }

    public String getGaKeywords() {
        return gaKeywords;
    }

    public void setGaKeywords(String gaKeywords) {
        this.gaKeywords = gaKeywords;
    }

    public String getGaCampaign() {
        return gaCampaign;
    }

    public void setGaCampaign(String gaCampaign) {
        this.gaCampaign = gaCampaign;
    }

    public String getGaUserId() {
        return gaUserId;
    }

    public void setGaUserId(String gaUserId) {
        this.gaUserId = gaUserId;
    }

    public String getGaTimespent() {
        return gaTimespent;
    }

    public void setGaTimespent(String gaTimespent) {
        this.gaTimespent = gaTimespent;
    }
    
}
