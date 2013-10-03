package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.resource.NamedResource;

/**
 * Model object for widget resource
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "widgets")
@ResourceMetaInfo(name = "Widget")
public class Widget implements NamedResource{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@FieldMetaInfo( displayName = "Widget Id",  description = "Widget Id")
	private Integer id;
	
	@Column(name = "name")
	@FieldMetaInfo( displayName = "Widget Name",  description = "Widget Name")
	private String name;
	
	@Column(name = "tag")
	@FieldMetaInfo( displayName = "Widget Tag",  description = "Widget Tag")
	private String tag;
	
	@Column(name = "content")
	@FieldMetaInfo( displayName = "Widget Content",  description = "Widget Content")
	private String content;
	
	@Column(name = "type")
	@FieldMetaInfo( displayName = "Widget Type",  description = "Widget Type")
	private String type;
	
	@Column(name = "created_at")
	@FieldMetaInfo( displayName = "Created Time",  description = "Created Time")
	private Date createdAt;
	
	@Column(name = "updated_at")
	@FieldMetaInfo( displayName = "Updated Time",  description = "Updated Time")
	private Date updatedAt;
	
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@PreUpdate
    public void preUpdate(){
    	updatedAt = new Date();
    }
    @PrePersist
    public void prePersist(){
    	createdAt = new Date();
    	updatedAt = createdAt;
    }
	
}
