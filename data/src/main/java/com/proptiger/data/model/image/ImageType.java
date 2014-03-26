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
import com.proptiger.data.model.ObjectType;

@Entity(name = "ImageType")
@JsonFilter("fieldFilter")
public class ImageType extends BaseModel {

    private static final long serialVersionUID = 6121401801684707486L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long              id;

    @ManyToOne(targetEntity = ObjectType.class, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "ObjectType_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ObjectType        objectType;

    @Column(name = "ObjectType_id")
    private String            objectTypeId;

    @Column(name = "type")
    private String            type;

    @Column(name = "priority")
    private int               priority;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
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
    public String getObjectTypeId() {
        return objectTypeId;
    }

    /**
     * @param objectTypeId
     *            the objectTypeId to set
     */
    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
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

}
