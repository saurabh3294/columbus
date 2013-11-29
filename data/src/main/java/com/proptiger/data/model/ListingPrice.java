package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "cms.listing_prices")
public class ListingPrice {

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "listing_id")
	private Integer listingId;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "effective_date")
	private Date effectiveDate;
	
	@Column(name = "price_per_unit_area")
	private Double pricePerUnitArea;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "comment")
	private String comment;

	@OneToOne
	@JsonIgnore
	@JoinColumn(name = "listing_id", referencedColumnName="id",insertable=false, updatable=false)
	private Listing listing;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getListingId() {
		return listingId;
	}

	public void setListingId(Integer listingId) {
		this.listingId = listingId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Double getPricePerUnitArea() {
		return pricePerUnitArea;
	}

	public void setPricePerUnitArea(Double pricePerUnitArea) {
		this.pricePerUnitArea = pricePerUnitArea;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
