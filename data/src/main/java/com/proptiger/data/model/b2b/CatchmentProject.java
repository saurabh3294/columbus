package com.proptiger.data.model.b2b;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.proptiger.data.model.BaseModel;

public class CatchmentProject extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "catchment_id")
    private Integer catchmentId;
    
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "created_at")
    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCatchmentId() {
        return catchmentId;
    }

    public void setCatchmentId(Integer catchmentId) {
        this.catchmentId = catchmentId;
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
}