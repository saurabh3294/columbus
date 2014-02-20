package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "cms.academic_qualifications")
@JsonFilter("fieldFilter")
public class AcademicQualification extends BaseModel {

    private static final long serialVersionUID = 38036779720160982L;

    @Id
    @Column(name = "id")
    private Integer           id;

    @Column(name = "qualification")
    private String            qualification;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

}
