package com.proptiger.data.model.companyuser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.core.model.BaseModel;

/**
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "cms.project_assignment_rules")
@JsonFilter("fieldFilter")
public class ProjectAssignmentRule extends BaseModel {

    private static final long serialVersionUID = 2137392504773576918L;

    @Id
    @Column(name = "id")
    private Integer           id;

    @Column(name = "rule_name")
    private String            ruleName;

    @Column(name = "broker_id")
    private Integer           brokerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

}
