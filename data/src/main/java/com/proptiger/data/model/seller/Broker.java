package com.proptiger.data.model.seller;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

	@OneToMany(mappedBy = "broker", fetch = FetchType.EAGER)
	private Set<ProjectAssignmentRule> projectAssignmentRules;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "primary_address_id",  nullable = false, insertable = false, updatable = false)
	private Address primaryAddress;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fax_number_id",  nullable = false, insertable = false, updatable = false)
	private ContactNumber faxNumberDetail;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "primary_broker_contact_id",  nullable = false, insertable = false, updatable = false)
	private BrokerContact primaryBrokerContact;
	
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

	public Set<ProjectAssignmentRule> getProjectAssignmentRules() {
		return projectAssignmentRules;
	}

	public void setProjectAssignmentRules(
			Set<ProjectAssignmentRule> projectAssignmentRules) {
		this.projectAssignmentRules = projectAssignmentRules;
	}

	public Address getPrimaryAddress() {
		return primaryAddress;
	}

	public void setPrimaryAddress(Address primaryAddress) {
		this.primaryAddress = primaryAddress;
	}

	public ContactNumber getFaxNumberDetail() {
		return faxNumberDetail;
	}

	public void setFaxNumberDetail(ContactNumber faxNumberDetail) {
		this.faxNumberDetail = faxNumberDetail;
	}

	public BrokerContact getPrimaryBrokerContact() {
		return primaryBrokerContact;
	}

	public void setPrimaryBrokerContact(BrokerContact primaryBrokerContact) {
		this.primaryBrokerContact = primaryBrokerContact;
	}
	
	
}
