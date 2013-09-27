/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@Entity
@Table(name="FORUM_USER")
@ResourceMetaInfo(name = "ForumUser")
public class ForumUser {
    @Column(name = "USER_ID")
    @Id
    private long userId;
    
    @Column(name = "USERNAME")
    private String  username;
    
    @Column(name = "EMAIL")
    private String  email;
    
    @Column(name = "CONTACT")
    private long  contact;
    
    @Column(name = "PROVIDERID")
    private String  providerid;
    
    @Column(name = "PROVIDER")
    private String  provider;
    
    @Column(name = "FB_IMAGE_URL")
    private String  fbImageUrl;
    
    @Column(name = "IMAGE")
    private String  image;
    
    @Column(name = "PASSWORD")
    private String  password;
    
    @Column(name = "CITY")
    private String  city;
    
    @Column(name = "COUNTRY_ID")
    private int  countryId;
    
    @Column(name = "UNIQUE_USER_ID")
    private String  uniqueUserId;
    
    @Column(name = "CREATED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date  createdDate;
    
    @Column(name = "STATUS")
    private int status;
    
    @Column(name = "IS_SUBSCRIBED")
    private byte  isSubscribed;
    
    @Column(name = "UNSUBSCRIBED_AT")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date  unsubscribedAt;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

}
