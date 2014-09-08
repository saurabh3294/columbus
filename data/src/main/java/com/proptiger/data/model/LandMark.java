/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.image.Image;

/**
 * 
 * @author mukand
 */
@Entity
@Table(name = "cms.landmarks")
@ResourceMetaInfo
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("fieldFilter")
public class LandMark extends BaseModel {

    private static final long serialVersionUID = 5296461750469591496L;

    @FieldMetaInfo(displayName = "Id", description = "Id")
    @Column(name = "id")
    @Id
    @Field("LANDMARK_ID")
    private int               id;

    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name = "locality_id", insertable = false, updatable = false)
    @Deprecated
    private int               localityId;

    @FieldMetaInfo(displayName = "City Id", description = "City Id")
    @Column(name = "city_id")
    @Field("LANDMARK_CITY_ID")
    private int               cityId;

    @FieldMetaInfo(displayName = "Place Type Id", description = "Place Type Id")
    @Column(name = "place_type_id", insertable = false, updatable = false)
    private int               placeTypeId;

    @FieldMetaInfo(displayName = "Name", description = "Name")
    @Column(name = "name")
    @Field("LANDMARK_NAME")
    private String            name;

    @FieldMetaInfo(displayName = "Reference", description = "Reference")
    @Column(name = "reference")
    @JsonIgnore
    private String            reference;

    @FieldMetaInfo(displayName = "Google Place Id", description = "Google Place Id")
    @Column(name = "google_place_id")
    @JsonIgnore
    private String            googlePlaceId;

    @FieldMetaInfo(displayName = "Address", description = "Address")
    @Column(name = "address")
    @Field("LANDMARK_ADDRESS")
    private String            address;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Column(name = "latitude")
    @Field("LANDMARK_LATITUDE")
    private double             latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Column(name = "longitude")
    @Field("LANDMARK_LONGITUDE")
    private double            longitude;

    @FieldMetaInfo(displayName = "Phone Number", description = "Phone Number")
    @Column(name = "phone_number")
    @Field("LANDMARK_PHONE_NUMBER")
    private String            phoneNumber;

    @FieldMetaInfo(displayName = "Google Url", description = "Google Url")
    @Column(name = "google_url")
    private String            googleUrl;

    @FieldMetaInfo(displayName = "Website", description = "Website")
    @Column(name = "website")
    @Field("LANDMARK_WEBSITE_URL")
    private String            website;

    @FieldMetaInfo(displayName = "Vicinity", description = "Vicinity")
    @Column(name = "vicinity")
    @Field("LANDMARK_VICINITY")
    private String            vicinity;

    @FieldMetaInfo(displayName = "Details Info", description = "Details Info")
    @Column(name = "is_details")
    @JsonIgnore
    private Integer               isDetails;

    @FieldMetaInfo(displayName = "Other Details", description = "Other Details")
    @Column(name = "rest_details")
    @JsonIgnore
    private String            restDetails;

    @FieldMetaInfo(displayName = "Priority", description = "Priority")
    @Field("LANDMARK_PRIORITY")
    private int               priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "place_type_id", referencedColumnName = "id")
    private LandMarkTypes     localityAmenityTypes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id", referencedColumnName = "LOCALITY_ID")
    @Deprecated
    private Locality          locality;
    
    @Transient
    private List<Image>       images;

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getPlaceTypeId() {
        return placeTypeId;
    }

    public void setPlaceTypeId(int placeTypeId) {
        this.placeTypeId = placeTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Integer getIsDetails() {
        return isDetails;
    }

    public void setIsDetails(Integer isDetails) {
        this.isDetails = isDetails;
    }

    public String getRestDetails() {
        return restDetails;
    }

    public void setRestDetails(String restDetails) {
        this.restDetails = restDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LandMarkTypes getLocalityAmenityTypes() {
        return localityAmenityTypes;
    }

    public void setLocalityAmenityTypes(LandMarkTypes localityAmenityTypes) {
        this.localityAmenityTypes = localityAmenityTypes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
