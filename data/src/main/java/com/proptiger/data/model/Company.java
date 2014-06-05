package com.proptiger.data.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
