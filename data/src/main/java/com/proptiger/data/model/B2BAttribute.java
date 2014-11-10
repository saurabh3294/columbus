package com.proptiger.data.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.core.model.BaseModel;

@Entity
@Table(name = "cms.b2b_properties")
public class B2BAttribute extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer           id;

    private String            name;

    private String            value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
