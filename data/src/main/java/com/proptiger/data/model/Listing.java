package com.proptiger.data.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Rajeev Pandey
 *
 */
//@Entity
//@Table(name = "cms.listings")
//@JsonFilter("fieldFilter")
public class Listing extends BaseModel{
	private static final long serialVersionUID = -1212348039595611394L;

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "option_id")
	private Integer optionId;
	
	@Column(name = "phase_id")
	private Integer phaseId;
	
	@Column(name = "listing_category")
	private String listingCategory;
	
	@Column(name = "status")
	private String status;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "option_id", referencedColumnName="OPTIONS_ID",insertable=false, updatable=false)
	private Property property;
	
	@OneToMany(mappedBy = "listing", fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<ListingPrice> listingPrice;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOptionId() {
		return optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	public Integer getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Integer phaseId) {
		this.phaseId = phaseId;
	}

	public String getListingCategory() {
		return listingCategory;
	}

	public void setListingCategory(String listingCategory) {
		this.listingCategory = listingCategory;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<ListingPrice> getListingPrice() {
		return listingPrice;
	}

	public void setListingPrice(Set<ListingPrice> listingPrice) {
		this.listingPrice = listingPrice;
	}
	
	
}
