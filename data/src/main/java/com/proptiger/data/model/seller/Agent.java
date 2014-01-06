package com.proptiger.data.model.seller;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.enums.ActivationStatus;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.agents")
public class Agent implements BaseModel{

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private ActivationStatus status;

	@Column(name = "broker_id")
	private Integer brokerId;
	
	@Column(name = "academic_qualification_id")
	private Integer academicQualificationId;
	
	@Column(name = "rating")
	private Double rating;
	
	@Column(name = "seller_type")
	private String sellerType;
	
	@Column(name = "hierarchy_left")
	private Integer hirarchyLeft;
	
	@Column(name = "hierarchy_right")
	private Integer hirarchyRight;
	
	@Column(name = "active_since")
	private Date activeSince;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "broker_id",  nullable = false, insertable = false, updatable = false)
	private Broker broker;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ActivationStatus getStatus() {
		return status;
	}

	public void setStatus(ActivationStatus status) {
		this.status = status;
	}

	public Integer getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Integer brokerId) {
		this.brokerId = brokerId;
	}

	public Integer getAcademicQualificationId() {
		return academicQualificationId;
	}

	public void setAcademicQualificationId(Integer academicQualificationId) {
		this.academicQualificationId = academicQualificationId;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getSellerType() {
		return sellerType;
	}

	public void setSellerType(String sellerType) {
		this.sellerType = sellerType;
	}

	public Integer getHirarchyLeft() {
		return hirarchyLeft;
	}

	public void setHirarchyLeft(Integer hirarchyLeft) {
		this.hirarchyLeft = hirarchyLeft;
	}

	public Integer getHirarchyRight() {
		return hirarchyRight;
	}

	public void setHirarchyRight(Integer hirarchyRight) {
		this.hirarchyRight = hirarchyRight;
	}

	public Date getActiveSince() {
		return activeSince;
	}

	public void setActiveSince(Date activeSince) {
		this.activeSince = activeSince;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	
	
}
