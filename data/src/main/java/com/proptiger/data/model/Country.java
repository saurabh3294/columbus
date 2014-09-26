package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "COUNTRY")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Country extends BaseModel {

    private static final long serialVersionUID = 111838211107993072L;

    @Id
    @Column(name = "COUNTRY_ID")
    private Integer           countryId;

    private String            label;

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
