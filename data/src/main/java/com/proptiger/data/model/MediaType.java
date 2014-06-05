package com.proptiger.data.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model Object For Media Types
 * 
 * @author azi
 * 
 */

@Entity
@Table(name = "media_types")
public class MediaType extends BaseModel {
    private static final long                        serialVersionUID = 1L;

    @Id
    private Integer                                  id;

    @Enumerated(EnumType.STRING)
    private com.proptiger.data.enums.MediaType name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.proptiger.data.enums.MediaType getName() {
        return name;
    }

    public void setName(com.proptiger.data.enums.MediaType name) {
        this.name = name;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}