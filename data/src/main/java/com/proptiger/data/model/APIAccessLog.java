package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Model to store user access details like mixpanel, and fingerprints.
 * 
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "api_access_log")
public class APIAccessLog extends BaseModel {
    private static final long serialVersionUID = 8564785566779291287L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;

    @Column(name = "user_id")
    private Integer           userId;

    @Column(name = "email")
    private String            email;

    @Column(name = "access_hash")
    private String            accessHash;

    @Column(name = "user_ip")
    private String            userIp;

    @Column(name = "access_count")
    private long              accessCount;

    @Column(name = "user_agent")
    private String            userAgent;

    @Column(name = "created_at")
    private Date              createdAt;

    @Column(name = "updated_at")
    private Date              updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccessHash() {
        return accessHash;
    }

    public void setAccessHash(String mixpanelId) {
        this.accessHash = mixpanelId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public String updateAccessHash() {
        String hash = DigestUtils.md5Hex(getUserId() + getEmail() + getUserAgent());
        this.accessHash = hash;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof APIAccessLog) {
            APIAccessLog anotherObj = (APIAccessLog) obj;
            return this.accessHash.equals(anotherObj.getAccessHash());
        }
        return false;
    }
}
