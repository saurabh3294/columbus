package com.proptiger.data.model.image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.MediaType;
import com.proptiger.data.model.ObjectType;

/**
 * 
 * @author yugal
 * 
 * @author azi
 * 
 */
@Entity(name = "object_media_types")
@JsonFilter("fieldFilter")
public class ObjectMediaType extends BaseModel {

    private static final long serialVersionUID = 6121401801684707486L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               id;

    @ManyToOne(targetEntity = ObjectType.class, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "ObjectType_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ObjectType        objectType;

    @ManyToOne(targetEntity = MediaType.class, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "media_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private MediaType         mediaType;

    @Column(name = "ObjectType_id")
    private int               objectTypeId;

    @Column(name = "media_type_id")
    private int               mediaTypeId;

    @Column(name = "type")
    private String            type;

    @Column(name = "priority")
    private int               priority;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the objectType
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the objectTypeId
     */
    public int getObjectTypeId() {
        return objectTypeId;
    }

    /**
     * @param objectTypeId
     *            the objectTypeId to set
     */
    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(int mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}