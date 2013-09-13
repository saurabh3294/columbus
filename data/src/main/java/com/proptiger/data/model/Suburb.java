/**
 * 
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    private long id;

    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    private long cityId;

    @FieldMetaInfo( displayName = "Label",  description = "Suburb label")
    @Column(name = "LABEL")
    private String label;

    private City city;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
}
