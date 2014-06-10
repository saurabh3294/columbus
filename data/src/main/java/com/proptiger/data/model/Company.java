package com.proptiger.data.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.enums.CompanyType;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "cms.company")
public class Company extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Id
    private int               id;

    @Enumerated(EnumType.STRING)
    private CompanyType       type;

    private String            name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CompanyType getType() {
        return type;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
