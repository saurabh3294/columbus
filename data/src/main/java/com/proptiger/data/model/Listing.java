package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * 
 * @author azi
 * @author Rajeev
 * 
 */
@Entity
@Table(name = "cms.listings")
@JsonFilter("fieldFilter")
public class Listing extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Integer           id;

    @ManyToOne
    @JoinColumn(
            name = "option_id",
            referencedColumnName = "TYPE_ID",
            insertable = false,
            updatable = false,
            nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private Property          property;

    @Column(name = "phase_id")
    private Integer           phaseId;

    @Column(name = "status")
    private String            status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id", referencedColumnName = "listing_id")
    private ProjectSupply     projectSupply;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Integer getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Integer phaseId) {
        this.phaseId = phaseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProjectSupply getProjectSupply() {
        return projectSupply;
    }

    public void setProjectSupply(ProjectSupply projectSupply) {
        this.projectSupply = projectSupply;
    }
}