package com.proptiger.data.model.trend;

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
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.Gson;
import com.proptiger.data.annotations.ExcludeFromBeanCopy;
import com.proptiger.data.enums.Status;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.pojo.FIQLSelector;

/**
 * BebUserSelection model object
 * 
 * @author Aziitabh Ajit
 * 
 */

@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "user_graphs")
@JsonFilter("fieldFilter")
public class Graph extends BaseModel {
    private static final long serialVersionUID = -2683590682098190102L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;

    @ExcludeFromBeanCopy
    @Column(name = "user_id")
    private Integer           userId;

    @ExcludeFromBeanCopy
    @Column(name = "parent_id")
    private Integer           parentId;

    @ExcludeFromBeanCopy
    @Column(name = "parent_type")
    @Enumerated(EnumType.STRING)
    private PARENTTYPE        parentType;

    @Size(min = 1, max = 255, message = "Graph name should be between 1 to 255 characters")
    private String            name;

    @Enumerated(EnumType.STRING)
    private Status            status           = Status.Active;

    @Transient
    private FIQLSelector      filter;

    @JsonIgnore
    @Column(name = "filter")
    private String            stringFilter;

    @Column(name = "range_field")
    private String            rangeField;

    @Column(name = "range_value")
    private String            rangeValue;

    @ExcludeFromBeanCopy
    @Column(name = "created_at")
    private Date              createdAt        = new Date();

    @ExcludeFromBeanCopy
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public static enum PARENTTYPE {
        Catchment, Builder
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}