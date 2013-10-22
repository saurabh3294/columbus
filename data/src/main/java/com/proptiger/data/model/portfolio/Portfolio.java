package com.proptiger.data.model.portfolio;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.util.DoubletoIntegerConverter;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "Portfolio")
public class Portfolio {
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "originalVaue", description = "Original Vaue")
	@JsonSerialize(converter=DoubletoIntegerConverter.class)
	private double originalValue;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "currentValue", description = "Current Value")
	@JsonSerialize(converter=DoubletoIntegerConverter.class)
	private double currentValue;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
	private OverallReturn overallReturn;

	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "properties", description = "Properties")
	private List<PortfolioListing> listings;

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
	public List<PortfolioListing> getListings() {
		return listings;
	}

	/**
	 * @param listings the listings to set
	 */
	public void setListings(List<PortfolioListing> listings) {
		this.listings = listings;
	}

	public void addListings(PortfolioListing listing){
		if(this.listings == null){
			this.listings = new ArrayList<PortfolioListing>();
		}
		this.listings.add(listing);
	}
	
}
