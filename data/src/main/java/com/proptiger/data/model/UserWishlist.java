package com.proptiger.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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

	@ManyToMany(targetEntity = ProjectDB.class)
	@JoinColumn(name = "projectId", referencedColumnName = "PROJECT_ID", table = "RESI_PROJECT")
	private Set<ProjectDB> projectDB;
	
	
	@ManyToMany(targetEntity = ProjectTypes.class)
	@JoinColumn(name = "typeId", referencedColumnName = "TYPE_ID")
	private Set<ProjectTypes> projectTypes;


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


}
