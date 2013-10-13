package com.proptiger.data.model.image;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.util.ImageUtil;

@Entity(name = "Image")
@Access(AccessType.FIELD)
public class Image {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "ImageType_id", insertable=false, updatable=false)
	private ImageType imageType;

	@Column(name = "ImageType_id")
    private long imageTypeId;
	
	@Column(name = "object_id")
	private long objectId;
	
	private String path;

    public void assignWatermarkName() {
        waterMarkName = id + ".jpg";
    }

    public void assignOriginalName(String format) {
        originalName = originalHash + '.' + format;
    }

	@JsonProperty
    public String getAbsolutePath() {
        return ImageUtil.endpoint + "/" + ImageUtil.bucket + "/" + path + waterMarkName;
    }

	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "taken_at", nullable = true)
	private Date takenAt;
	
	@Column(name = "size_in_bytes")
	private long sizeInBytes;
	
	private int width;
	
	private int height;
	
	private Double latitude;
	
	private Double longitude;
	
	@Column(name = "alt_text", nullable = true)
	private String altText;
	
	private String title;
	
	private String description;
	
	@Column(name = "json_dump", nullable = true)
	private String jsonDump;
	
	private Integer priority;

	@Column(name = "original_hash")
	@JsonIgnore
	private String originalHash;

	@Column(name = "original_name")
    @JsonIgnore
    private String originalName;

	@JsonIgnore
	@Column(name = "watermark_hash")
	private String waterMarkHash;

	@JsonIgnore
	@Column(name = "watermark_name")
	private String waterMarkName;

	private boolean active;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public long getImageTypeId() {
        return imageTypeId;
    }

    public void setImageTypeId(long imageTypeId) {
        this.imageTypeId = imageTypeId;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJsonDump() {
        return jsonDump;
    }

    public void setJsonDump(String jsonDump) {
        this.jsonDump = jsonDump;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getOriginalHash() {
        return originalHash;
    }

    public void setOriginalHash(String originalHash) {
        this.originalHash = originalHash;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getWaterMarkHash() {
        return waterMarkHash;
    }

    public void setWaterMarkHash(String waterMarkHash) {
        this.waterMarkHash = waterMarkHash;
    }

    public String getWaterMarkName() {
        return waterMarkName;
    }

    public void setWaterMarkName(String waterMarkName) {
        this.waterMarkName = waterMarkName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
