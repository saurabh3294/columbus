package com.proptiger.data.internal.dto.mail;

import com.proptiger.data.model.City;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;

public class LeadSubmitMail {

    private Project  project;
    private Locality locality;
    private City     city;
    private Enquiry  enquiry;
    private String  leadMailFlag;
    private String  projectsDetail;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Enquiry getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(Enquiry enquiry) {
        this.enquiry = enquiry;
    }

    public String getLeadMailFlag() {
        return leadMailFlag;
    }

    public void setLeadMailFlag(String leadMailFlag) {
        this.leadMailFlag = leadMailFlag;
    }

    public String getProjectsDetail() {
        return projectsDetail;
    }

    public void setProjectsDetail(String projectsDetail) {
        this.projectsDetail = projectsDetail;
    }
}
