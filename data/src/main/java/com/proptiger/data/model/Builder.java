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

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
public class Builder implements BaseModel {
    @FieldMetaInfo(displayName = "Builder Id",  description = "Builder Id")
    @Column(name = "BUILDER_ID")
    @Id
    @Field("BUILDER_ID")
    private int id;

    @FieldMetaInfo( displayName = "Name",  description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    @Field("BUILDER_NAME")
    private String name;

    @FieldMetaInfo( displayName = "Image",  description = "Builder Image URL")
    @Transient
    private String imageURL;
    
    @FieldMetaInfo( displayName = "Description",  description = "Description")
    @Column(name = "DESCRIPTION")
    @Field("BUILDER_DESCRIPTION")
    private String description;
    
    @Transient
    @Field("ESTABLISHED_DATE")
    private Date estabilishedDate;
    
    @Transient
    private Integer totalProjects;
    @Transient
    private Integer totalOngoingProjects;
    @Transient
    private List<Project> projects;
    
    @Column(name="DISPLAY_ORDER")
    @Field("BUILDER_PRIORITY")
    private Integer priority;
    
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

	public Integer getTotalProjects() {
		return totalProjects;
	}

	public void setTotalProjects(Integer totalProjects) {
		this.totalProjects = totalProjects;
	}

	public Integer getTotalOngoingProjects() {
		return totalOngoingProjects;
	}

	public void setTotalOngoingProjects(Integer totalOngoingProjects) {
		this.totalOngoingProjects = totalOngoingProjects;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
    
}
