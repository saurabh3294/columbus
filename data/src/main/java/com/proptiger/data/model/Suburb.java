/**
 * 
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
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
@Table(name = "SUBURB")
@ResourceMetaInfo(name = "Suburb")
public class Suburb {
    @Id
    @FieldMetaInfo(name = "suburbId", displayName = "Suburb Id", dataType = DataType.LONG, description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    @JsonProperty
    private long id;

    @FieldMetaInfo(name = "cityId", displayName = "City Id", dataType = DataType.LONG, description = "City Id")
    @Column(name = "CITY_ID")
    @JsonProperty
    private long cityId;

    @FieldMetaInfo(name = "label", displayName = "Label", dataType = DataType.STRING, description = "Suburb label")
    @Column(name = "LABEL")
    @JsonProperty
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
