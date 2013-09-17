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

@Entity(name = "Image")
public class Image implements Serializable {
	@Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "ImageType_id")
	private ImageType imageType;
	
	@Column(name = "object_id")
	private int objectId;
	
	@Column(name = "path")
	private String path;
	
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
	
	@Column(name = "format")
	private String format;
	
	@Column(name = "order", nullable = true)
	private Integer order;
	
	@Column(name = "content_hash")
	private String contentHash;
	
	@Column(name = "seo_name")
	private String seoName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getPath() {
        return path;
    }

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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getSeoName() {
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

}
