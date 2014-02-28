package com.proptiger.data.model.b2b;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.BaseModel;

/**
 * BebUserSelection model object
 * 
 * @author Aziitabh Ajit
 * 
 */

@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_user_catchments")
@JsonFilter("fieldFilter")
public class Graph extends BaseModel {
    @Id
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "catchment_id")
    private Integer catchmentId;

    private String  graph;

    private String  name;

    private STATUS  status;

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

    public Integer getCatchmentId() {
        return catchmentId;
    }

    public void setCatchmentId(Integer catchmentId) {
        this.catchmentId = catchmentId;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
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