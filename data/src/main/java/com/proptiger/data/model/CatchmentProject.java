package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author azi
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_catchment_projects")
@JsonFilter("fieldFilter")
public class CatchmentProject extends BaseModel {
    private static final long serialVersionUID = -6020089914804953739L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catchment_id")
    @JsonIgnore
    private Catchment         catchment;

    @Column(name = "project_id")
    private Integer           projectId;

    @Column(name = "created_at")
    private Date              createdAt        = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Catchment getCatchment() {
        return catchment;
    }

    public void setCatchment(Catchment catchment) {
        this.catchment = catchment;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}