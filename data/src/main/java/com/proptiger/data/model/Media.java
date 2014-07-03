package com.proptiger.data.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;
import com.proptiger.data.annotations.ExcludeFromBeanCopy;
import com.proptiger.data.model.image.ObjectMediaType;
import com.proptiger.data.util.MediaUtil;

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
    @ExcludeFromBeanCopy
    private Integer           id;

    @Column(name = "object_media_type_id")
    @ExcludeFromBeanCopy
    private Integer           objectMediaTypeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "object_media_type_id", insertable = false, updatable = false)
    @ExcludeFromBeanCopy
    private ObjectMediaType   objectMediaType;

    @Column(name = "object_id")
    @ExcludeFromBeanCopy
    private Integer           objectId;

    @JsonIgnore
    @ExcludeFromBeanCopy
    private String            url;

    @Min(value = 1, message = "Priority can't be less than 1")
    @Max(value = 999, message = "Priority can't be more than 999")
    private int               priority         = 1;

    @Transient
    @ExcludeFromBeanCopy
    private String            absoluteUrl;

    @Column(name = "size_in_bytes")
    @ExcludeFromBeanCopy
    private Long              sizeInBytes;

    private String            description;

    @JsonIgnore
    @ExcludeFromBeanCopy
    @Column(name = "media_extra_attributes")
    private String            stringMediaExtraAttributes;

    @Transient
    private JsonNode          mediaExtraAttributes;

    @JsonIgnore
    @ExcludeFromBeanCopy
    @Column(name = "content_hash")
    private String            contentHash;

    @ExcludeFromBeanCopy
    private boolean           active;

    @Column(name = "created_at")
    @ExcludeFromBeanCopy
    private Date              createdAt        = new Date();

    @Column(name = "updated_at")
    @ExcludeFromBeanCopy
    private Date              updatedAt        = new Date();

    @OneToOne(optional = true)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    @ExcludeFromBeanCopy
    private AudioAttributes   audioAttributes;

    @PostLoad
    private void postLoad() {
        this.absoluteUrl = MediaUtil.getMediaEndpoint(this.id) + "/" + this.url;
        extractAndSetExtraAttributesFromString();
    }

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
        extractAndSetExtraAttributesFromString();
    }

    public JsonNode getMediaExtraAttributes() {
        return mediaExtraAttributes;
    }

    public void setMediaExtraAttributes(JsonNode mediaExtraAttributes) {
        this.mediaExtraAttributes = mediaExtraAttributes;
        this.stringMediaExtraAttributes = mediaExtraAttributes.toString();
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public ObjectMediaType getObjectMediaType() {
        return objectMediaType;
    }

    public void setObjectMediaType(ObjectMediaType objectMediaType) {
        this.objectMediaType = objectMediaType;
    }

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public AudioAttributes getAudioAttributes() {
        return audioAttributes;
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
    }
    
    private void extractAndSetExtraAttributesFromString()
    {
        try {
            this.mediaExtraAttributes = JsonLoader.fromString(this.stringMediaExtraAttributes);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}