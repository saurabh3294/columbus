package com.proptiger.data.model.image;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFilter;

@Entity(name = "image_qualities")
@Access(AccessType.FIELD)
@JsonFilter("fieldFilter")
public class ImageQuality {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int               id;

    @Column(name = "quality")
    private float             quality;

    @Column(name = "image_resolutions_id")
    private int               resolutionId;

    @Column(name = "image_types_Id")
    private int               imageTypeId;

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public int getImageTypeId() {
        return imageTypeId;
    }
    
    

    public int getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(int resolutionId) {
        this.resolutionId = resolutionId;
    }

    public void setImageTypeId(int imageTypeId) {
        this.imageTypeId = imageTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
