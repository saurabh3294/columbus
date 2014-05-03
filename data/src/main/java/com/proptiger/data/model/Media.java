package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
public class Media {
    @Id
    private Integer         id;

    @Column(name = "object_media_type_id")
    private Integer         objectMediaTypeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "object_media_type_id", insertable = false, updatable = false)
    private ObjectMediaType objectMediaType;

    @Column(name = "object_id")
    private Integer         objectId;

    @Column(name = "size_in_bytes")
    private Long            sizeInBytes;

    private String          description;

    @Column(name = "media_extra_attributes")
    private String          mediaExtraAttributes;

    @Transient
    private JsonNode        jsonMediaExtraAttributes;

    private Integer         priority;

    @Column(name = "content_hash")
    private String          contentHash;

    @Column(name = "active")
    private Boolean         isActive;

    private String          extension;

    @Column(name = "file_creation_time")
    private Date            fileCreationTime;

    @Column(name = "created_at")
    private Date            createdAt;

    @Column(name = "updated_at")
    private Date            updatedAt;

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

    public String getMediaExtraAttributes() {
        return mediaExtraAttributes;
    }

    public void setMediaExtraAttributes(String mediaExtraAttributes) {
        this.mediaExtraAttributes = mediaExtraAttributes;
    }

    public JsonNode getJsonMediaExtraAttributes() {
        return jsonMediaExtraAttributes;
    }

    public void setJsonMediaExtraAttributes(JsonNode jsonMediaExtraAttributes) {
        this.jsonMediaExtraAttributes = jsonMediaExtraAttributes;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
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

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
