/**
 * 
 */
package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author mandeep
 *
 */
@Entity
@Table(name = "SUBURB")
@ResourceMetaInfo
//@JsonFilter("fieldFilter")
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
    @Field("DESCRIPTION")
    @FieldMetaInfo( displayName = "Description",  description = "Description")
    private String description;

    @Column(name="PRIORITY")
    @Field("SUBURB_PRIORITY")
    @FieldMetaInfo( displayName = "Priority",  description = "Priority")
    private int priority;
    
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
}
