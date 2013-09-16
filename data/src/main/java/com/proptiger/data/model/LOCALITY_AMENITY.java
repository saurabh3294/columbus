/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import com.proptiger.data.meta.FieldMetaInfo;
import javax.persistence.Column;

/**
 *
 * @author mukand
 */
public class LOCALITY_AMENITY {
    @FieldMetaInfo(displayName = "Locality Id", description = "Locality Id")
    @Column(name="locality_id")
    private int  localityId;

    @FieldMetaInfo(displayName = "City Id", description = "City Id")
    @Column(name="city_id")
    private int  cityId;
    
    @FieldMetaInfo(displayName = "Place Type Id", description = "Place Type Id")
    @Column(name="place_type_id")
    private int  placeTypeId; 
    
    @FieldMetaInfo(displayName = "Name", description = "Name")
    @Column(name="name")
    private String  name;

    @FieldMetaInfo(displayName = "Reference", description = "Reference")
    @Column(name="reference")
    private String  reference;

    @FieldMetaInfo(displayName = "Google Place Id", description = "Google Place Id")
    @Column(name="google_place_id")
    private String  googlePlaceId;

    @FieldMetaInfo(displayName = "Address", description = "Address")
    @Column(name="address")
    private String  address;

    @FieldMetaInfo(displayName = "Latitude", description = "Latitude")
    @Column(name="latitude")
    private float  latitude;

    @FieldMetaInfo(displayName = "Longitude", description = "Longitude")
    @Column(name="longitude")
    private float  longitude;

    @FieldMetaInfo(displayName = "Phone Number", description = "Phone Number")
    @Column(name="phone_number")
    private String  phoneNumber;

    @FieldMetaInfo(displayName = "Google Url", description = "Google Url")
    @Column(name="google_url")
    private String  googleUrl;

    @FieldMetaInfo(displayName = "Website", description = "Website")
    @Column(name="website")
    private String  website;
    
    @FieldMetaInfo(displayName = "Vicinity", description = "Vicinity")
    @Column(name="vicinity")
    private String  vicinity;

    @FieldMetaInfo(displayName = "Details Info", description = "Details Info")
    @Column(name="is_details")
    private short  isDetails;

    @FieldMetaInfo(displayName = "Other Details", description = "Other Details")
    @Column(name="rest_details")
    private String  restDetails;

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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
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

    public short getIsDetails() {
        return isDetails;
    }

    public void setIsDetails(short isDetails) {
        this.isDetails = isDetails;
    }

    public String getRestDetails() {
        return restDetails;
    }

    public void setRestDetails(String restDetails) {
        this.restDetails = restDetails;
    }


}