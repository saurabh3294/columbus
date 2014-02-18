package com.proptiger.data.internal.dto.mail;

/**
 * @author Rajeev Pandey
 * 
 */
public class ListingResaleMail {

    private String userName;
    private String email;
    private String mobile;
    private String propertyName;
    private String projectCity;
    private String projectName;
    private String locality;
    private String builder;
    private String propertyLink;
    private Double listingSize;
    private String measure;
    private String unitName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getProjectCity() {
        return projectCity;
    }

    public void setProjectCity(String projectCity) {
        this.projectCity = projectCity;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getPropertyLink() {
        return propertyLink;
    }

    public void setPropertyLink(String propertyLink) {
        this.propertyLink = propertyLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Double getListingSize() {
        return listingSize;
    }

    public void setListingSize(Double listingSize) {
        this.listingSize = listingSize;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

}
