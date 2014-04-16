package com.proptiger.data.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@ResourceMetaInfo
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("fieldFilter")
public class LandMarkResult extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 6426964388112360892L;

    private LandMarkTypes     localityAmenityTypes;
    private LandMark          localityAmenity;

    public LandMarkResult() {
        localityAmenityTypes = new LandMarkTypes();
        localityAmenity = new LandMark();
        localityAmenity.setLocalityAmenityTypes(localityAmenityTypes);
    }

    @Field("LANDMARK_ID")
    private int          id;

    @Field("LANDMARK_CITY_ID")
    private int          cityId;

    @Field("LANDMARK_NAME")
    private String       name;

    @Field("LANDMARK_ADDRESS")
    private String       address;

    @Field("LANDMARK_LATITUDE")
    private double       latitude;

    @Field("LANDMARK_LONGITUDE")
    private double       longitude;

    @Field("LANDMARK_PHONE_NUMBER")
    private String       phoneNumber;

    @Field("LANDMARK_WEBSITE_URL")
    private String       website;

    @Field("LANDMARK_VICINITY")
    private String       vicinity;

    @Field("LANDMARK_PRIORITY")
    private int          priority;

    @Field("LANDMARK_TYPE")
    private String       amenityType;

    @Field("LANDMARK_DESCRIPTION")
    private String       amenityDescription;

    @Field("GEO")
    private List<String> geo;

    @Field("HAS_GEO")
    private int          hasGeo;

    @Field("geodist()")
    private Double       geoDistance;

    public LandMarkTypes getLocalityAmenityTypes() {
        return localityAmenityTypes;
    }

    public void setLocalityAmenityTypes(LandMarkTypes localityAmenityTypes) {
        this.localityAmenityTypes = localityAmenityTypes;
    }

    public LandMark getLocalityAmenity() {
        return localityAmenity;
    }

    public void setLocalityAmenity(LandMark localityAmenity) {
        this.localityAmenity = localityAmenity;
    }

    @Field("LANDMARK_ID")
    public void setId(int id) {
        localityAmenity.setId(id);
    }

    @Field("LANDMARK_CITY_ID")
    public void setCityId(int cityId) {
        localityAmenity.setCityId(cityId);
    }

    @Field("LANDMARK_NAME")
    public void setName(String name) {
        localityAmenity.setName(name);
    }

    @Field("LANDMARK_ADDRESS")
    public void setAddress(String address) {
        localityAmenity.setAddress(address);
    }

    @Field("LANDMARK_LATITUDE")
    public void setLatitude(double latitude) {
        localityAmenity.setLatitude(latitude);
    }

    @Field("LANDMARK_LONGITUDE")
    public void setLongitude(double longitude) {
        localityAmenity.setLongitude(longitude);
    }

    @Field("LANDMARK_PHONE_NUMBER")
    public void setPhoneNumber(String phoneNumber) {
        localityAmenity.setPhoneNumber(phoneNumber);
    }

    @Field("LANDMARK_WEBSITE_URL")
    public void setWebsite(String website) {
        localityAmenity.setWebsite(website);
    }

    @Field("LANDMARK_VICINITY")
    public void setVicinity(String vicinity) {
        localityAmenity.setVicinity(vicinity);
    }

    @Field("LANDMARK_PRIORITY")
    public void setPriority(int priority) {
        localityAmenity.setPriority(priority);
    }

    @Field("LANDMARK_TYPE")
    public void setAmenityType(String amenityType) {
        localityAmenityTypes.setName(amenityType);
    }

    @Field("LANDMARK_DESCRIPTION")
    public void setAmenityDescription(String amenityDescription) {
        localityAmenityTypes.setDescription(amenityDescription);
        ;
    }
}
