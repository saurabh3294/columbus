package com.proptiger.data.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "subscription_type")
public class SubscriptionType extends BaseModel {

    private static final long serialVersionUID = -3042431508853104170L;
    
    @Id
    @Column(name = "id")
    private int               id;

    @Column(name = "name")
    private String            name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
