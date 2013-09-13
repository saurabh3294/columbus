/**
 * 
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author mandeep
 *
 */
@Entity
@Table(name = "RESI_BUILDER")
@ResourceMetaInfo(name = "Builder")
public class Builder {
    @FieldMetaInfo(displayName = "Builder Id",  description = "Builder Id")
    @Column(name = "BUILDER_ID")
    @Id
    private long id;

    @FieldMetaInfo( displayName = "Name",  description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    private String name;

    @FieldMetaInfo( displayName = "Image",  description = "Builder Image URL")
    @Column(name = "BUILDER_IMAGE")
    private String imageUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
