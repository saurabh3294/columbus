package com.proptiger.data.model.b2b;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.pojo.FIQLSelector;

/**
 * Catchment model object
 * 
 * @author Aziitabh Ajit
 * 
 */

@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_user_catchments")
@JsonFilter("fieldFilter")
public class Catchment extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String  catchment;

    private String  name;

    @Enumerated(EnumType.STRING)
    private STATUS  status = STATUS.Active;

    @Column(name = "created_at")
    private Date    createdAt;

    @Column(name = "updated_at")
    private Date    updatedAt;

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

    public String getCatchment() {
        return catchment;
    }

    public void setCatchment(FIQLSelector selector) {
        this.catchment = selector.getFilters();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
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
}