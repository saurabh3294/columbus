/**
 * 
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author mandeep
 *
 */
@Entity
@Table(name = "SUBURB")
@ResourceMetaInfo(name = "Suburb")
public class Suburb {
    @Id
    @FieldMetaInfo( displayName = "Suburb Id",  description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    private int id;

    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    private int cityId;

    @FieldMetaInfo( displayName = "Label",  description = "Suburb label")
    @Column(name = "LABEL")
    private String label;

    @ManyToOne
    @JoinColumn(name="CITY_ID", insertable = false, updatable = false)
    private City city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
