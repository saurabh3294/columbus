/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.model.user.Dashboard;

/**
 * Use User model instead of this 
 * @author mukand
 */
@Entity
@Table(name = "FORUM_USER")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
@Deprecated
public class ForumUser extends BaseModel {
    public static final String USER_STATUS_ACTIVE = "1";

    private static final long             serialVersionUID = 6769127512697320945L;

    @Column(name = "USER_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer                       userId;

    @Column(name = "USERNAME")
    private String                        username;

    @Column(name = "EMAIL")
    @JsonIgnore
    private String                        email;

    @Column(name = "CONTACT")
    @JsonIgnore
    private long                          contact;

    @Column(name = "PROVIDERID")
    private String                        providerid;

    @Column(name = "PROVIDER")
    private String                        provider;

    @Column(name = "FB_IMAGE_URL")
    private String                        fbImageUrl;

    @Column(name = "IMAGE")
    private String                        image;

    @JsonIgnore
    @Column(name = "PASSWORD")
    private String                        password;

    @Column(name = "CITY")
    private String                        city;

    @Column(name = "COUNTRY_ID")
    private int                           countryId;

    @Column(name = "UNIQUE_USER_ID")
    private String                        uniqueUserId;

    @Column(name = "CREATED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date                          createdDate;

    @Column(name = "STATUS")
    private String                        status;

    @Column(name = "IS_SUBSCRIBED")
    private byte                          isSubscribed;

    @Column(name = "UNSUBSCRIBED_AT")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date                          unsubscribedAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "userId", fetch = FetchType.EAGER)
    private List<UserSubscriptionMapping> userSubscriptionMappings;

    @OneToMany(mappedBy = "userId")
    private List<Dashboard>               dashboards;

    @JsonIgnore
    @OneToMany(mappedBy = "userId")
    private List<UserPreference>          preferences;

    @JsonProperty
    public String getImageUrl() {
        return this.fbImageUrl + this.image;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFbImageUrl() {
        return fbImageUrl;
    }

    public void setFbImageUrl(String fbImageUrl) {
        this.fbImageUrl = fbImageUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public byte getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(byte isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public Date getUnsubscribedAt() {
        return unsubscribedAt;
    }

    public void setUnsubscribedAt(Date unsubscribedAt) {
        this.unsubscribedAt = unsubscribedAt;
    }

    public List<UserSubscriptionMapping> getUserSubscriptionMappings() {
        return userSubscriptionMappings;
    }

    public void setUserSubscriptionMappings(List<UserSubscriptionMapping> userSubscriptionMappings) {
        this.userSubscriptionMappings = userSubscriptionMappings;
    }

    public List<Dashboard> getDashboards() {
        return dashboards;
    }

    public void setDashboards(List<Dashboard> dashboards) {
        this.dashboards = dashboards;
    }

    public List<UserPreference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<UserPreference> preferences) {
        this.preferences = preferences;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    @PrePersist
    public void prePersist(){
        this.createdDate = new Date();
    }
}
