/**
 * 
 */
package com.proptiger.data.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;


/**
 * @author mandeep
 *
 */
@Entity
@Table(name = "SUBURB")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Suburb implements BaseModel {
    @Id
    @FieldMetaInfo( displayName = "Suburb Id",  description = "Suburb Id")
    @Column(name = "SUBURB_ID")
    @Field("SUBURB_ID")
    private int id;

    @FieldMetaInfo( displayName = "City Id",  description = "City Id")
    @Column(name = "CITY_ID")
    @Field("CITY_ID")
    private int cityId;

    @FieldMetaInfo( displayName = "Label",  description = "Suburb label")
    @Column(name = "LABEL")
    @Field("SUBURB")
    private String label;

    @ManyToOne(fetch=FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name="CITY_ID", insertable = false, updatable = false)
    private City city;
    
    @Column(name="URL")
    @Field("SUBURB_URL")
    @FieldMetaInfo( displayName = "URL",  description = "URL")
    private String url;
    
    @Column(name="DESCRIPTION")
    @Field("SUBURB_DESCRIPTION")
    @FieldMetaInfo( displayName = "Description",  description = "Description")
    private String description;

    @Column(name="PRIORITY")
    @Field("SUBURB_PRIORITY")
    @FieldMetaInfo( displayName = "Priority",  description = "Priority")
    private int priority;

    @Transient
    @Field("SUBURB_PRICE_PER_UNIT_AREA")
    private Double avgPricePerUnitArea;

    @Transient
    @Field("SUBURB_PRICE_RISE")
    private Double avgPriceRisePercentage;

    @Transient
    @Field("SUBURB_PRICE_RISE_TIME")
    private Integer avgPriceRiseMonths;
    
    @Transient
    @Field("SUBURB_DOMINANT_UNIT_TYPE")
    private String dominantUnitType;
    
    @Transient
    private Map<Integer, Double> avgBHKPrice;
        
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

    public Double getAvgPriceRisePercentage() {
        return avgPriceRisePercentage;
    }

    public void setAvgPriceRisePercentage(Double avgPriceRisePercentage) {
        this.avgPriceRisePercentage = avgPriceRisePercentage;
    }

    public Integer getAvgPriceRiseMonths() {
        return avgPriceRiseMonths;
    }

    public void setAvgPriceRiseMonths(Integer avgPriceRiseMonths) {
        this.avgPriceRiseMonths = avgPriceRiseMonths;
    }

    public Double getAvgPricePerUnitArea() {
        return avgPricePerUnitArea;
    }

    public void setAvgPricePerUnitArea(Double avgPricePerUnitArea) {
        this.avgPricePerUnitArea = avgPricePerUnitArea;
    }

	public String getDominantUnitType() {
		return dominantUnitType;
	}

	public void setDominantUnitType(String dominantUnitType) {
		this.dominantUnitType = dominantUnitType;
	}

	public Map<Integer, Double> getAvgBHKPrice() {
		return avgBHKPrice;
	}

	public void setAvgBHKPrice(Map<Integer, Double> avgBHKPrice) {
		this.avgBHKPrice = avgBHKPrice;
	}
}
