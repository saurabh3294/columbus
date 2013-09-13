/**
 * 
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;

/**
 * @author mandeep
 *
 */
public class Builder {
    @FieldMetaInfo(name = "builderId", displayName = "Builder Id", dataType = DataType.LONG, description = "Builder Id")
    @Column(name = "BUILDER_ID")
    @Id
    @JsonProperty
    private long id;

    @FieldMetaInfo(name = "name", displayName = "Name", dataType = DataType.STRING, description = "Builder Name")
    @Column(name = "BUILDER_NAME")
    @JsonProperty
    private String name;

    @FieldMetaInfo(name = "image", displayName = "Image", dataType = DataType.STRING, description = "Builder Image URL")
    @Column(name = "BUILDER_IMAGE")
    @JsonProperty
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
