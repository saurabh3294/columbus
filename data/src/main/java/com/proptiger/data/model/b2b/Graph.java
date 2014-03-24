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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.Gson;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.pojo.FIQLSelector;

/**
 * BebUserSelection model object
 * 
 * @author Aziitabh Ajit
 * 
 */

@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_graphs")
@JsonFilter("fieldFilter")
public class Graph extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer      id;

    @Column(name = "user_id")
    private Integer      userId;

    @Column(name = "parent_id")
    private Integer      parentId;

    @Column(name = "parent_type")
    private PARENTTYPE   parentType;

    private String       name;

    @Enumerated(EnumType.STRING)
    private STATUS       status    = STATUS.Active;

    @Transient
    private FIQLSelector filter;

    @JsonIgnore
    @Column(name = "filter")
    private String       stringFilter;

    @Column(name = "range_field")
    private String       rangeField;

    @Column(name = "range_value")
    private String       rangeValue;

    @Column(name = "created_at")
    private Date         createdAt = new Date();

    @Column(name = "updated_at")
    private Date         updatedAt;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public PARENTTYPE getParentType() {
        return parentType;
    }

    public void setParentType(PARENTTYPE parentType) {
        this.parentType = parentType;
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

    public FIQLSelector getFilter() {
        return filter;
    }

    public void setFilter(FIQLSelector filter) {
        Gson gson = new Gson();
        this.stringFilter = gson.toJson(filter);
        this.filter = filter;
    }

    public String getStringFilter() {
        return stringFilter;
    }

    public void setStringFilter(String stringFilter) {
        Gson gson = new Gson();
        this.filter = gson.fromJson(stringFilter, FIQLSelector.class);
        this.stringFilter = stringFilter;
    }

    public String getRangeField() {
        return rangeField;
    }

    public void setRangeField(String rangeField) {
        this.rangeField = rangeField;
    }

    public String getRangeValue() {
        return rangeValue;
    }

    public void setRangeValue(String rangeValue) {
        this.rangeValue = rangeValue;
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

    public enum PARENTTYPE {
        Catchment, Builder
    }
}