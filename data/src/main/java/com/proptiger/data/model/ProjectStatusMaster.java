package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cms.project_status_master")
public class ProjectStatusMaster extends BaseModel {

    private static final long serialVersionUID = -1689938553944928175L;

    @Column(name = "ID")
    @Id
    private int               Id;

    @Column(name = "PROJECT_STATUS")
    private String            projectStatus;

    @Column(name = "DISPLAY_NAME")
    private String            displayName;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
