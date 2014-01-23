package com.proptiger.data.model.portfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;

@Entity
@Table(name = "subscription_type")
public class SubscriptionType implements BaseModel {
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;

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
