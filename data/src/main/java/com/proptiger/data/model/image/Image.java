package com.proptiger.data.model.image;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name = "Image")
public class Image implements Serializable {
	@Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	@Transient
	@JoinColumn(name = "ImageType_id")
	private ImageType imageType;
	
	@Column(name = "ImageType_id")
	private long imageTypeId;
	
	@Column(name = "object_id")
	private long objectId;
	
	@Column(name = "path")
	private String path;
	
	@JsonProperty
	public String getSeoPath() {
		return path + seoName;
	}
	
	@JsonProperty
	public String getContentPath() {
		return path + contentName;
	}
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "taken_at", nullable = true)
	private Date takenAt;
	
	@Column(name = "size")
	private long size;
	
	@Column(name = "width")
	private int width;
	
	@Column(name = "height")
	private int height;
	
	@Column(name = "label")
	private String label;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "priority", nullable = true)
	private Integer priority;
	
	@Column(name = "content_name")
	private String contentName;
	
	@Column(name = "seo_name")
	private String seoName;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the imageType
	 */
	public ImageType getImageType() {
		return imageType;
	}

	/**
	 * @param imageType the imageType to set
	 */
	public void setImageType(ImageType imageType) {
		this.imageType = imageType;
	}

	/**
	 * @return the imageTypeId
	 */
	public long getImageTypeId() {
		return imageTypeId;
	}

	/**
	 * @param imageTypeId the imageTypeId to set
	 */
	public void setImageTypeId(long imageTypeId) {
		this.imageTypeId = imageTypeId;
	}

	/**
	 * @return the objectId
	 */
	public long getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
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
	 * @return the takenAt
	 */
	public Date getTakenAt() {
		return takenAt;
	}

	/**
	 * @param takenAt the takenAt to set
	 */
	public void setTakenAt(Date takenAt) {
		this.takenAt = takenAt;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the contentName
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * @param contentName the contentName to set
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	/**
	 * @return the seoName
	 */
	public String getSeoName() {
		return seoName;
	}

	/**
	 * @param seoName the seoName to set
	 */
	public void setSeoName(String seoName) {
		this.seoName = seoName;
	}

}
