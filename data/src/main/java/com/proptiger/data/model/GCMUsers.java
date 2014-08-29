package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "gcm.gcm_users")
public class GCMUsers extends BaseModel {

    private static final long serialVersionUID = 7829394463604901690L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int               id;

    @Column(name = "gcm_regid")
    private String            gcmRegId;

    @Column(name = "name")
    private String            name;

    @Column(name = "email")
    private String            email;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdAt;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
