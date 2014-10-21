package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;

@Entity
@Table(name = "ENQUIRY_ATTRIBUTES")
@JsonFilter("fieldFilter")
public class EnquiryAttributes extends BaseModel {
    private static final long serialVersionUID = -7418341728261456163L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;

    @Column(name = "ENQUIRY_ID")
    private long              enquiryId;

    @Column(name = "TYPE_ID")
    private Integer           typeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(long enquiryId) {
        this.enquiryId = enquiryId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

}
