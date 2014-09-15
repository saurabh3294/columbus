package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.proptiger.data.enums.AndroidApplication;

@Entity
@Table(name = "gcm.gcm_users")
public class GCMUser extends BaseModel {

    private static final long  serialVersionUID = 7829394463604901690L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int                id;

    @Column(name = "gcm_regid")
    private String             gcmRegId;

    @Column(name = "email")
    private String             email;

    @Column(name = "app_identifier")
    @Enumerated(EnumType.STRING)
    private AndroidApplication appIdentifier;

    @Column(name = "user_id")
    private Integer            userId;

    @Column(name = "login_status", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean            loginStatus;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date               createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AndroidApplication getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(AndroidApplication appIdentifier) {
        this.appIdentifier = appIdentifier;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
