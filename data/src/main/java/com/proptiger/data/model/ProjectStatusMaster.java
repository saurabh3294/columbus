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
    private String            project_status;

    @Column(name = "DISPLAY_NAME")
    private String            display_name;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProject_status() {
        return project_status;
    }

    public void setProject_status(String project_status) {
        this.project_status = project_status;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

}
