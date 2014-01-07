package com.proptiger.data.model.seller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.enums.BrokerType;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.broker_contacts")
public class BrokerContact  implements BaseModel{

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "broker_id")
	private Integer brokerId;
	
	@Column(name = "name")
	private String name;

	@Column(name = "contact_number_id")
	private Integer contactNumberId;
	
	@Column(name = "contact_email")
	private String email;
	
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private BrokerType type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Integer brokerId) {
		this.brokerId = brokerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getContactNumberId() {
		return contactNumberId;
	}

	public void setContactNumberId(Integer contactNumberId) {
		this.contactNumberId = contactNumberId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BrokerType getType() {
		return type;
	}

	public void setType(BrokerType type) {
		this.type = type;
	}
	
	
}
