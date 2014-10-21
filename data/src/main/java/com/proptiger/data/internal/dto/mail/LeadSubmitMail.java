package com.proptiger.data.internal.dto.mail;

import java.util.List;

import com.proptiger.data.model.City;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;

public class LeadSubmitMail {

    private Enquiry       enquiry;
    private String        leadMailFlag;
    private String        projectsDetail;
    private List<Project> projects;


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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
