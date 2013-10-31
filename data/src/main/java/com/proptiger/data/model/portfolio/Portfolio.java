package com.proptiger.data.model.portfolio;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.util.DoubletoIntegerConverter;

/**
 * Portfolio model
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo
public class Portfolio {
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "originalVaue", description = "Original Vaue")
	@JsonSerialize(converter=DoubletoIntegerConverter.class)
	private double originalValue;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "currentValue", description = "Current Value")
	@JsonSerialize(converter=DoubletoIntegerConverter.class)
	private double currentValue;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
	@JsonUnwrapped
	private OverallReturn overallReturn;

	@JsonIgnore
	private List<PortfolioListing> portfolioListings;

	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "Listing Ids", description = "Listing Ids")
	private List<Integer> listings;
	
	public double getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(double originalValue) {
		this.originalValue = originalValue;
	}

	/**
	 * @return the currentValue
	 */
	public double getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(double currentValue) {
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
