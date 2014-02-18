package com.proptiger.data.internal.dto;

/**
 * This class will serve as a input to fetch project price trend
 * 
 * @author Rajeev Pandey
 * 
 */
public class ProjectPriceTrendInput {

    private Integer projectId;
    private Integer typeId;
    private String  listingName;
    private String  projectName;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getListingName() {
        return listingName;
    }

    public void setListingName(String name) {
        this.listingName = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
