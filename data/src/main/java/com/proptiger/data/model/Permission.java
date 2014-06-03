package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.proptiger.data.enums.DataAccessLevel;
import com.proptiger.data.enums.DomainObject;

/**
 * 
 * @author azi
 * 
 */
@Entity(name = "permissions")
public class Permission extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Column(name = "object_type_id")
    private int               objectTypeId;

    @Column(name = "object_id")
    private int               objectId;

    @Column(name = "access_level")
    private DataAccessLevel   accessLevel;

    @Column(name = "object_type_id")
    @Enumerated(EnumType.STRING)
    private DomainObject      objectType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public DataAccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(DataAccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public DomainObject getObjectType() {
        return objectType;
    }

    public void setObjectType(DomainObject objectType) {
        this.objectType = objectType;
    }
}