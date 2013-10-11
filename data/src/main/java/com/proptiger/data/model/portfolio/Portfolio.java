package com.proptiger.data.model.portfolio;

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
	private double originalVaue;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "currentValue", description = "Current Value")
	@JsonSerialize(converter=DoubletoIntegerConverter.class)
	private double currentValue;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
	private OverallReturn overallReturn;

	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "properties", description = "Properties")
	private List<PortfolioProperty> properties;

	/**
	 * @return the originalVaue
	 */
	public double getOriginalVaue() {
		return originalVaue;
	}

	/**
	 * @param originalVaue the originalVaue to set
	 */
	public void setOriginalVaue(double originalVaue) {
		this.originalVaue = originalVaue;
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
	 * @return the properties
	 */
	public List<PortfolioProperty> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<PortfolioProperty> properties) {
		this.properties = properties;
	}
	
	
	
}
