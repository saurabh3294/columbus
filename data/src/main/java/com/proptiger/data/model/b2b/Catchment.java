package com.proptiger.data.model.b2b;

import java.io.IOException;
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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;
import com.proptiger.data.model.BaseModel;
import com.proptiger.exception.ProAPIException;

/**
 * Catchment model object
 * 
 * @author Aziitabh Ajit
 * 
 */

@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_user_catchments")
@JsonFilter("fieldFilter")
public class Catchment extends BaseModel {
    private static final long      serialVersionUID  = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer                id;

    @Column(name = "user_id")
    private Integer                userId;

    private String                 name;

    @Size(min = 1, message = "Catchment can't be empty")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "catchment", cascade = CascadeType.ALL)
    private List<CatchmentProject> catchmentProjects = new ArrayList<>();

    @JsonIgnore
    @Column(name = "meta_attributes")
    private String                 stringMetaAttributes;

    @Transient
    private JsonNode               metaAttributes;

    @Enumerated(EnumType.STRING)
    private STATUS                 status            = STATUS.Active;

    @Column(name = "created_at")
    private Date                   createdAt         = new Date();

    @Column(name = "updated_at")
    private Date                   updatedAt;

    @PostLoad
    public void setJsonPreference() {
        if (this.stringMetaAttributes != null) {
            try {
                this.metaAttributes = JsonLoader.fromString(this.stringMetaAttributes);
            }
            catch (IOException e) {
                throw new ProAPIException(e);
            }
        }
    }

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

    public String getStringMetaAttributes() {
        return stringMetaAttributes;
    }

    public void setStringMetaAttributes(String stringMetaAttributes) {
        try {
            this.metaAttributes = JsonLoader.fromString(stringMetaAttributes);
        }
        catch (IOException e) {
            throw new ProAPIException(e);
        }
        this.stringMetaAttributes = stringMetaAttributes;
    }

    public JsonNode getMetaAttributes() {
        return metaAttributes;
    }

    public void setMetaAttributes(JsonNode metaAttributes) {
        this.metaAttributes = metaAttributes;
        this.stringMetaAttributes = metaAttributes.toString();
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

    public static long getSerialversionuid() {
        return serialVersionUID;
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