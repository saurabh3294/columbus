package com.proptiger.data.model.b2b;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.BaseModel;

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
    private Integer                id;

    @Column(name = "user_id")
    private Integer                userId;

    private String                 name;

    @Size(min = 1, message = "Catchment can't be empty")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "catchment", cascade = CascadeType.ALL)
    private List<CatchmentProject> catchmentProjects = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private STATUS                 status            = STATUS.Active;

    @Column(name = "created_at")
    private Date                   createdAt         = new Date();

    @Column(name = "updated_at")
    private Date                   updatedAt;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CatchmentProject> getCatchmentProjects() {
        return catchmentProjects;
    }

    public void setCatchmentProjects(List<CatchmentProject> catchmentProjects) {
        this.catchmentProjects = catchmentProjects;
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

    public List<Integer> getProjectIds() {
        List<Integer> projectIds = new ArrayList<>();
        for (CatchmentProject catchmentProject : catchmentProjects) {
            projectIds.add(catchmentProject.getProjectId());
        }
        return projectIds;
    }

    public List<CatchmentProject> deleteProjectIds(List<Integer> projectIds) {
        List<CatchmentProject> newCatchmentProjects = new ArrayList<>();
        List<CatchmentProject> deletedCatchmentProjects = new ArrayList<>();
        for (CatchmentProject catchmentProject : catchmentProjects) {
            if (projectIds.contains(catchmentProject.getProjectId())) {
                deletedCatchmentProjects.add(catchmentProject);
            }
            else {
                newCatchmentProjects.add(catchmentProject);
            }
        }
        catchmentProjects = newCatchmentProjects;
        return deletedCatchmentProjects;
    }

    public List<CatchmentProject> addProjectIds(List<Integer> projectIds) {
        List<Integer> allProjectIds = getProjectIds();
        List<CatchmentProject> addedCatchmentProjects = new ArrayList<>();
        for (Integer projectId : projectIds) {
            if (!allProjectIds.contains(projectId)) {
                CatchmentProject catchmentProject = new CatchmentProject();
                catchmentProject.setProjectId(projectId);
                addedCatchmentProjects.add(catchmentProject);
                catchmentProjects.add(catchmentProject);
            }
        }
        return addedCatchmentProjects;
    }
}