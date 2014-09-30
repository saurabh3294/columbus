/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.ws.rs.DefaultValue;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.enums.lead.BuyPeriod;
import com.proptiger.data.enums.lead.ProcessingStatus;
import com.proptiger.data.enums.lead.SalesType;

/**
 * 
 * @author mukand
 */

@Entity
@Table(name = "ENQUIRY")
@JsonFilter("fieldFilter")
public class Enquiry extends BaseModel {

    private static final long serialVersionUID = 8405769379921577431L;
    @Column(name = "ID")
    @Id
    private long              id;
    @Column(name = "NAME")
    private String            name;
    
    @Column(name = "email")
    private String            email;
    @Column(name = "PHONE")
    private String            phone;
    
    @Transient
    private Integer           countryId;
    
    @Column(name = "COUNTRY_OF_RESIDENCE")
    private String            country;
    
    @Column(name = "QUERY")
    private String            query;

//    @JsonProperty("project_id")
    @Column(name = "PROJECT_ID")
    private Integer           projectId;
    
    @Transient
    private List<Integer>           multipleProjectIds;

  //  @JsonProperty("project_name")
    @Column(name = "PROJECT_NAME")
    private String            projectName;
    
    @Transient
    private String            projectUrl;
    
    @Transient
    private Integer           typeId;

    //@JsonProperty("locality_id")
 //   @JsonProperty(required = false,value = "locality_id" )
    @Column(name = "LOCALITY_ID")
    private Integer           localityId;
    
    @Transient
    private String            localityName;
    
    @Transient
    private String            localityUrl;
    
    @Column(name = "CITY_ID")
    private Integer           cityId;

    //@JsonProperty("city_name")
    @Column(name = "CITY_NAME")
    private String            cityName;
    
    @Transient
    private String            cityUrl;
    
    @Transient
    private String            builderName;
 
    @Column(name = "IP")
    private String            ip;

    @Transient
    private Boolean           resaleAndLaunchFlag; // true to 1

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date              createdDate;
    
    @Column(name = "SOURCE")
    private String            source;
    
    @Column(name = "FORM_NAME")
    private String            formName;
    
    @Column(name = "PAGE_NAME")
    private String            pageName;
    
    @Column(name = "PAGE_URL")
    private String            pageUrl;
    
    @Column(name = "REF_URL")
    private String            landingPage;

    @Column(name = "AD_GRP")
    private String            adGrp;
    
    @Column(name = "KEYWORDS")
    private String            keywords;
    
    @Column(name = "CAMPAIGN")
    private String            campaign;
    
    @Column(name = "PPC")
    private Boolean            ppc;
    
    @Column(name = "USER")
    private String            user;
    
    @Column(name = "HTTP_REFERER")
    private String            httpReferer;
    
    @Column(name = "USER_MEDIUM")
    private String            userMedium;

    //@JsonProperty("source")
    @Column(name = "GA_SOURCE")
    private String            gaSource;

    //@JsonProperty("user_medium")
    @Column(name = "GA_MEDIUM")
    private String            gaMedium;

    //@JsonProperty("user_network")
    @Column(name = "GA_NETWORK")
    private String            gaNetwork;
    
    @Column(name = "GA_KEYWORDS")
    private String            gaKeywords;
    
    @Column(name = "GA_CAMPAIGN")
    private String            gaCampaign;
    
    @Column(name = "GA_USER_ID")
    private String            gaUserId;
    
    @Column(name = "GA_TIMESPENT")
    private String            gaTimespent;
    

    @Column(name = "GA_PPC")
    private int            gaPpc;

    @Column(name = "PROCESSING_STATUS")
    @Enumerated(EnumType.STRING)
    private   ProcessingStatus          processingStatus  =  ProcessingStatus.unsuccessful ;
    
    @Column(name = "REGISTERED_USER")
    private String registeredUser   = "";
    
    @Column(name = "LEAD_SALE_TYPE")
    @Enumerated(EnumType.STRING)
    private SalesType            salesType  =   SalesType.primary;

    //@JsonProperty("application_type")
    @Column(name = "APPLICATION_TYPE")
    private String            applicationType;
    
    // Lead Query fields
    @Transient
    private String  bedrooms;

    @Transient
    private Boolean  budgetFlag;
    
    @Transient
    private String  budget;
    
    @Transient
    private Boolean  homeLoanTypeFlag;
    
    @Transient
    private String  homeLoanType;
    
    @Transient
    private String  propertyType;
    
    @Transient
    private Boolean  buyPeriodFlag;
    
    @Column(name = "BUY_PERIOD")
    @Enumerated(EnumType.ORDINAL)
    private BuyPeriod  buyPeriod;
    
    @Transient
    private String  json;
    
    @Transient
    private String  leadMailFlag;
    
    @Transient
    private String  buySell;
    
    @ManyToOne
    @JoinColumn(name = "LOCALITY_ID", referencedColumnName = "LOCALITY_ID", insertable = false, updatable = false)
    @JsonIgnore
    private Locality          locality;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private Project         project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID", insertable = false, updatable = false)
    @JsonIgnore
    private City            city;
    
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

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getLocalityUrl() {
        return localityUrl;
    }

    public void setLocalityUrl(String localityUrl) {
        this.localityUrl = localityUrl;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityUrl() {
        return cityUrl;
    }

    public void setCityUrl(String cityUrl) {
        this.cityUrl = cityUrl;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getResaleAndLaunchFlag() {
        return resaleAndLaunchFlag;
    }

    public void setResaleAndLaunchFlag(Boolean resaleAndLaunchFlag) {
        this.resaleAndLaunchFlag = resaleAndLaunchFlag;
    }

    public SalesType getSalesType() {
        return salesType;
    }

    public void setSalesType(SalesType salesType) {
        this.salesType = salesType;
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

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
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

    public Boolean getPpc() {
        return ppc;
    }

    public void setPpc(Boolean ppc) {
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

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
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

    public int getGaPpc() {
        return gaPpc;
    }

    public void setGaPpc(int gaPpc) {
        this.gaPpc = gaPpc;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getHomeLoanType() {
        return homeLoanType;
    }

    public void setHomeLoanType(String homeLoanType) {
        this.homeLoanType = homeLoanType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getLeadMailFlag() {
        return leadMailFlag;
    }

    public void setLeadMailFlag(String leadMailFlag) {
        this.leadMailFlag = leadMailFlag;
    }

    public String getBuySell() {
        return buySell;
    }

    public void setBuySell(String buySell) {
        this.buySell = buySell;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Integer> getMultipleProjectIds() {
        return multipleProjectIds;
    }

    public void setMultipleProjectIds(List<Integer> multipleProjectIds) {
        this.multipleProjectIds = multipleProjectIds;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(String registeredUser) {
        this.registeredUser = registeredUser;
    }

    public Boolean getBudgetFlag() {
        return budgetFlag;
    }

    public void setBudgetFlag(Boolean budgetFlag) {
        this.budgetFlag = budgetFlag;
    }

    public Boolean getHomeLoanTypeFlag() {
        return homeLoanTypeFlag;
    }

    public void setHomeLoanTypeFlag(Boolean homeLoanTypeFlag) {
        this.homeLoanTypeFlag = homeLoanTypeFlag;
    }

    public Boolean getBuyPeriodFlag() {
        return buyPeriodFlag;
    }

    public void setBuyPeriodFlag(Boolean buyPeriodFlag) {
        this.buyPeriodFlag = buyPeriodFlag;
    }

    public BuyPeriod getBuyPeriod() {
        return buyPeriod;
    }

    public void setBuyPeriod(BuyPeriod buyPeriod) {
        this.buyPeriod = buyPeriod;
    }

    
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @PrePersist
    public void prePersist() {
        createdDate = new Date();
    }
    
    public static class EnquiryCustomDetails extends BaseModel {
        private static final long serialVersionUID = -8600077968473619387L;
        private String            projectName;
        private String            cityName;
        private String            projectUrl;
        private Date              createdDate;
        private String            builderName;

        public EnquiryCustomDetails(
                String projectName,
                String cityName,
                String projectUrl,
                Date createdDate,
                String builderName) {
            super();
            this.projectName = projectName;
            this.cityName = cityName;
            this.projectUrl = projectUrl;
            this.createdDate = createdDate;
            this.builderName = builderName;
        }

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getCityName() {
            return cityName;
        }

        public String getProjectUrl() {
            return projectUrl;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public String getBuilderName() {
            return builderName;
        }

    }

    public BeanstalkEnquiry createBeanstalkEnquiryObj() {

        BeanstalkEnquiry beanstalkEnquiry = new BeanstalkEnquiry();

        beanstalkEnquiry.setId(this.getId());
        beanstalkEnquiry.setName(this.getName());
        beanstalkEnquiry.setApplicationType(this.getApplicationType());
        // beanstalkEnquiry.setCityName(cityName);
        beanstalkEnquiry.setCountry(this.getCountry());
        beanstalkEnquiry.setDeadReason("None");
        beanstalkEnquiry.setDescription(this.getQuery());
        beanstalkEnquiry.setEnquiryTime(this.getCreatedDate());
        beanstalkEnquiry.setEmail(this.getEmail());
        // beanstalkEnquiry.setFollowUpDate(this.getCreatedDate());
        beanstalkEnquiry.setLeadOwner("leadowner");
        beanstalkEnquiry.setLeadOwnerId("442");
        beanstalkEnquiry.setLeadType("none");
        // beanstalkEnquiry.setLocality(locality);
        beanstalkEnquiry.setLocalityId(this.getLocalityId());
        beanstalkEnquiry.setPhone(this.getPhone());
        beanstalkEnquiry.setProjectId(this.getProjectId());
        // beanstalkEnquiry.setProjectName(projectName);
        beanstalkEnquiry.setQuery(this.getQuery());
        beanstalkEnquiry.setReferrer("");
        beanstalkEnquiry.setReferrerLeadId("");
        beanstalkEnquiry.setSource(this.gaSource);
        beanstalkEnquiry.setStatus("New");
        beanstalkEnquiry.setSubbrokerDetails("");
        beanstalkEnquiry.setUserMedium(this.getGaMedium());
        beanstalkEnquiry.setUserNetwork(this.getGaNetwork());
                
        if(this.getPageName() == "CONTACT US") {
            beanstalkEnquiry.setLeadTag("contact");
        }
        else if(this.getSalesType() == SalesType.resale) {
            beanstalkEnquiry.setLeadTag("resale");
        }
        else if (this.getPageName() == "HOMELOAN" || this.getSalesType() == SalesType.homeloan) {
            beanstalkEnquiry.setLeadTag("homeloan");
        }
        else {
            beanstalkEnquiry.setLeadTag("");
        }
        return beanstalkEnquiry;

    }
}
