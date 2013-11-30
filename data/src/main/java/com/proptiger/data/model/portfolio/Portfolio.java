package com.proptiger.data.model.portfolio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Portfolio model
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class Portfolio {
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "originalVaue", description = "Original Vaue")
	private BigDecimal originalValue;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "currentValue", description = "Current Value")
	private BigDecimal currentValue;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
	@JsonUnwrapped
	private OverallReturn overallReturn;

	@JsonIgnore
	private List<PortfolioListing> portfolioListings;

	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "Listing Ids", description = "Listing Ids")
	private List<Integer> listings;
	
	public BigDecimal getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(BigDecimal originalValue) {
		this.originalValue = originalValue;
	}

	/**
	 * @return the currentValue
	 */
	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * @return the overallReturn
	 */
	public OverallReturn getOverallReturn() {
		return overallReturn;
	}

	/**
	 * @param overallReturn the overallReturn to set
	 */
	public void setOverallReturn(OverallReturn overallReturn) {
		this.overallReturn = overallReturn;
	}

	/**
	 * @return the listings
	 */
	public List<PortfolioListing> getPortfolioListings() {
		return portfolioListings;
	}

	/**
	 * @param listings the listings to set
	 */
	public void setPortfolioListings(List<PortfolioListing> listings) {
		this.portfolioListings = listings;
	}

	public void addPortfolioListings(PortfolioListing listing){
		if(this.portfolioListings == null){
			this.portfolioListings = new ArrayList<PortfolioListing>();
		}
		this.portfolioListings.add(listing);
	}

	public List<Integer> getListings() {
		return listings;
	}

	public void setListings(List<Integer> listings) {
		this.listings = listings;
	}
	
	public void addListings(Integer listingId){
		if(this.listings == null){
			this.listings = new ArrayList<>();
		}
		this.listings.add(listingId);
	}
}
