package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.proptiger.data.model.image.ObjectMediaType;

/**
 * Model object for media
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "media")
public class Media extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;

    @Column(name = "object_media_type_id")
    private Integer           objectMediaTypeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "object_media_type_id", insertable = false, updatable = false)
    private ObjectMediaType   objectMediaType;

    @Column(name = "object_id")
    private Integer           objectId;

    private String            url;

    @JsonIgnore
    @Column(name = "original_file_name")
    private String            originalFileName;

    @Column(name = "size_in_bytes")
    private Long              sizeInBytes;

    private String            description;

    @JsonIgnore
    @Column(name = "media_extra_attributes")
    private String            stringMediaExtraAttributes;

    @Transient
    private JsonNode          mediaExtraAttributes;

    @JsonIgnore
    @Column(name = "content_hash")
    private String            contentHash;

    @Column(name = "active")
    private Boolean           isActive;

    @Column(name = "file_creation_time")
    private Date              fileCreationTime;

    @Column(name = "created_at")
    private Date              createdAt        = new Date();

    @Column(name = "updated_at")
    private Date              updatedAt        = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getObjectMediaTypeId() {
        return objectMediaTypeId;
    }

    public void setObjectMediaTypeId(Integer objectMediaTypeId) {
        this.objectMediaTypeId = objectMediaTypeId;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStringMediaExtraAttributes() {
        return stringMediaExtraAttributes;
    }

    public void setStringMediaExtraAttributes(String stringMediaExtraAttributes) {
        this.stringMediaExtraAttributes = stringMediaExtraAttributes;
    }

    public JsonNode getMediaExtraAttributes() {
        return mediaExtraAttributes;
    }

    public void setMediaExtraAttributes(JsonNode mediaExtraAttributes) {
        this.mediaExtraAttributes = mediaExtraAttributes;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getFileCreationTime() {
        return fileCreationTime;
    }

    public void setFileCreationTime(Date fileCreationTime) {
        this.fileCreationTime = fileCreationTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}