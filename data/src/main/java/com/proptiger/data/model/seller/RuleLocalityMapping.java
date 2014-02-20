package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Mapping of a locality with rule id of ProjectAssignmentRule
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "cms.rule_locality_mappings")
@JsonFilter("fieldFilter")
public class RuleLocalityMapping {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "locality_id")
    private Integer localityId;

    @Column(name = "rule_id")
    private Integer ruleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

}
