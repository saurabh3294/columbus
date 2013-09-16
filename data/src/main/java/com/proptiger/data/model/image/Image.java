package com.proptiger.data.model.image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

@Entity(name = "Image")
public class Image {
	@Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "ImageType_id")
	private ImageType imageType;
	
	@Column(name = "object_id")
	private long objectId;
	
	@Column(name = "path")
	private String path;
	
	@Column(name = "created_at")
	private DateTime createdAt;
	
	@Column(name = "taken_at")
	private DateTime takenAt;
	
	@Column(name = "size")
	private long size;
	
	@Column(name = "width")
	private int width;
	
	@Column(name = "height")
	private int height;
	
	@Column(name = "format")
	private String format;
	
	@Column(name = "order")
	private Integer order;
	
	@Column(name = "content_hash")
	private String contentHash;
	
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
	public DateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the takenAt
	 */
	public DateTime getTakenAt() {
		return takenAt;
	}

	/**
	 * @param takenAt the takenAt to set
	 */
	public void setTakenAt(DateTime takenAt) {
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
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @return the contentHash
	 */
	public String getContentHash() {
		return contentHash;
	}

	/**
	 * @param contentHash the contentHash to set
	 */
	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
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
