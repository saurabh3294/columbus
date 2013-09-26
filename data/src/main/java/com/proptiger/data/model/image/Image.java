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

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@JsonIgnore
	public String getOriginalPath() {
		return path + originalName;
	}
	
	@JsonProperty
	public String getWaterMarkPath() {
		return path + waterMarkName;
	}
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "taken_at", nullable = true)
	private Date takenAt;
	
	@Column(name = "size_in_bytes")
	private long sizeInBytes;
	
	@Column(name = "width")
	private int width;
	
	@Column(name = "height")
	private int height;
	
	@Column(name = "alt_text", nullable = true)
	private String altText;
	
	@Column(name = "label", nullable = true)
	private String label;
	
	@Column(name = "description", nullable = true)
	private String description;
	
	@Column(name = "priority", nullable = true)
	private Integer priority;
	
	@Column(name = "original_hash")
	private String originalHash;
	
	@Column(name = "watermark_hash")
	private String waterMarkHash;
	
	@Column(name = "original_name")
	private String originalName;
	
	@Column(name = "watermark_name")
	private String waterMarkName;

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
	 * @return the sizeInBytes
	 */
	public long getSizeInBytes() {
		return sizeInBytes;
	}

	/**
	 * @param sizeInBytes the sizeInBytes to set
	 */
	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
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
	 * @return the altText
	 */
	public String getAltText() {
		return altText;
	}

	/**
	 * @param altText the altText to set
	 */
	public void setAltText(String altText) {
		this.altText = altText;
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
	 * @return the originalHash
	 */
	public String getOriginalHash() {
		return originalHash;
	}

	/**
	 * @param originalHash the originalHash to set
	 */
	public void setOriginalHash(String originalHash) {
		this.originalHash = originalHash;
	}

	/**
	 * @return the waterMarkHash
	 */
	public String getWaterMarkHash() {
		return waterMarkHash;
	}

	/**
	 * @param waterMarkHash the waterMarkHash to set
	 */
	public void setWaterMarkHash(String waterMarkHash) {
		this.waterMarkHash = waterMarkHash;
	}

	/**
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * @return the waterMarkName
	 */
	public String getWaterMarkName() {
		return waterMarkName;
	}

	/**
	 * @param waterMarkName the waterMarkName to set
	 */
	public void setWaterMarkName(String waterMarkName) {
		this.waterMarkName = waterMarkName;
	}

}
