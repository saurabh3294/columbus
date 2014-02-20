package com.proptiger.data.internal.dto;

/**
 * Unmatched project details
 * 
 * @author Rajeev Pandey
 * 
 */
public class UnmatchedProjectDetails {
    private String projectName;
    private String projectCity;
    private String locality;

    private String userName;
    private String userEmail;
    private long   contact;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCity() {
        return projectCity;
    }

    public void setProjectCity(String projectCity) {
        this.projectCity = projectCity;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

}
