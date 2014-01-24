package com.proptiger.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User wish list model object corresponding to USER_WISHLIST table
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "USER_WISHLIST")
public class UserWishlist implements Serializable{
	private static final long serialVersionUID = -5523514441836021198L;

	@Id
	@GeneratedValue
    @Column(name="ID")
	private Integer id;
	
	@Column(name = "USER_ID")
	private Integer userId; 
	
	@Column(name = "PROJECT_ID")
	private Integer projectId; 
	
	@Column(name = "TYPE_ID")
	private Integer typeId; 
	
	@Column(name = "DATETIME")
	private Date datetime;

	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JsonIgnore
	@JoinColumn(name = "PROJECT_ID", insertable=false, updatable=false)
	private Project project;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "TYPE_ID", insertable=false, updatable=false)
	@JsonIgnore
	private Property property;


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


	public Integer getProjectId() {
		return projectId;
	}


	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}


	public Integer getTypeId() {
		return typeId;
	}


	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}


	public Date getDatetime() {
		return datetime;
	}


	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}


	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}


	public Property getProperty() {
		return property;
	}


	public void setProperty(Property property) {
		this.property = property;
	}
}
