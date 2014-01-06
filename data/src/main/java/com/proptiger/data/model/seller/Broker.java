package com.proptiger.data.model.seller;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.enums.ActivationStatus;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.brokers")
public class Broker  implements BaseModel{
	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "broker_name")
	private String brokerName;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private ActivationStatus status;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "primary_address_id")
	private Integer primaryAddressId;
	
	@Column(name = "fax_number_id")
	private Integer faxNumberId;
	
	@Column(name = "primary_email")
	private String primaryEmail;
	
	@Column(name = "primary_broker_contact_id")
	private Integer primaryBrokerContactId;
	
	@Column(name = "pan")
	private String pan;
	
	@Column(name = "active_since")
	private Date activeSince;
	
	@Column(name = "cc_contact_id")
	private Integer ccContactId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public ActivationStatus getStatus() {
		return status;
	}

	public void setStatus(ActivationStatus status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPrimaryAddressId() {
		return primaryAddressId;
	}

	public void setPrimaryAddressId(Integer primaryAddressId) {
		this.primaryAddressId = primaryAddressId;
	}

	public Integer getFaxNumberId() {
		return faxNumberId;
	}

	public void setFaxNumberId(Integer faxNumberId) {
		this.faxNumberId = faxNumberId;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	public Integer getPrimaryBrokerContactId() {
		return primaryBrokerContactId;
	}

	public void setPrimaryBrokerContactId(Integer primaryBrokerContactId) {
		this.primaryBrokerContactId = primaryBrokerContactId;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public Date getActiveSince() {
		return activeSince;
	}

	public void setActiveSince(Date activeSince) {
		this.activeSince = activeSince;
	}

	public Integer getCcContactId() {
		return ccContactId;
	}

	public void setCcContactId(Integer ccContactId) {
		this.ccContactId = ccContactId;
	}
	
	
}
