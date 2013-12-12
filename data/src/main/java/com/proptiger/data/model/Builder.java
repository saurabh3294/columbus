/**
 * 
 */
package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author mandeep
 *
 */
@Entity
@Table(name = "RESI_BUILDER")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class Builder implements BaseModel {
    @FieldMetaInfo(displayName = "Builder Id",  description = "Builder Id")
    @Column(name = "BUILDER_ID")
    @Id
    private int id;

    @FieldMetaInfo( displayName = "Name",  description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    private String name;

    @FieldMetaInfo( displayName = "Image",  description = "Builder Image URL")
    @Transient
    private String imageURL;
    
    @FieldMetaInfo( displayName = "Description",  description = "Description")
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Transient
    private Date estabilishedDate;
    @Transient
    private int derivedTotalProject;
    @Transient
    private int derivedTotalOngoingProject;
    @Transient
    private List<Project> derivedProjects;
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageUrl) {
        this.imageURL = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public Date getEstabilishedDate() {
		return estabilishedDate;
	}

	public void setEstabilishedDate(Date estabilishedDate) {
		this.estabilishedDate = estabilishedDate;
	}

	public int getDerivedTotalProject() {
		return derivedTotalProject;
	}

	public void setDerivedTotalProject(int derivedTotalProject) {
		this.derivedTotalProject = derivedTotalProject;
	}

	public int getDerivedTotalOngoingProject() {
		return derivedTotalOngoingProject;
	}

	public void setDerivedTotalOngoingProject(int derivedTotalOngoingProject) {
		this.derivedTotalOngoingProject = derivedTotalOngoingProject;
	}

	public List<Project> getDerivedProjects() {
		return derivedProjects;
	}

	public void setDerivedProjects(List<Project> derivedProjects) {
		this.derivedProjects = derivedProjects;
	}
    
}
