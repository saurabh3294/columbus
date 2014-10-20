package com.proptiger.data.model.image;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.model.BaseModel;

@Entity(name = "image_resolutions")
@Access(AccessType.FIELD)
@JsonFilter("fieldFilter")
public class ImageResolutionModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int               id;

    @Column(name = "resolution")
    private String            label;

    @Column(name = "width")
    private int               width;

    @Column(name = "height")
    private int               height;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
